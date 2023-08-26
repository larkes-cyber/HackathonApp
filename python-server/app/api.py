import base64 as base53

from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates
from fastapi.requests import Request
from fastapi.responses import FileResponse
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

# Доп. код для определения поз:
import cv2
import mediapipe as mp
from PIL import Image, ImageDraw
import numpy as np
import pickle
import math

mp_pose = mp.solutions.pose
pose = mp_pose.Pose(static_image_mode=True, model_complexity=1)
mp_drawing = mp.solutions.drawing_utils
with open('./model_pose_842.pkl', 'rb') as f:
    model_for_poses = pickle.load(f)
with open('./model_racurs.pkl', 'rb') as f:
    model_racurs = pickle.load(f)
num_block_access_cords = [8, 6, 5, 4, 1, 2, 3, 7, 18, 20, 22, 21, 19, 17]
num_access_cords = list(filter(lambda x: x not in num_block_access_cords, list(range(32))))


def distance(x1, y1, x2, y2):
    return math.sqrt((x2 - x1) ** 2 + (y2 - y1) ** 2)


def pic_to_cord(frame):
    results = pose.process(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))

    cord = list()
    for i in num_access_cords:
        try:
            x = results.pose_landmarks.landmark[mp_pose.PoseLandmark(i).value].x
            y = results.pose_landmarks.landmark[mp_pose.PoseLandmark(i).value].y
            if x == None or y == None:
                cord.append([0, 0])
            else:
                cord.append([x, y])
        except:
            cord.append([0, 0])

    return cord


def video_now(video_array):
    if not video_array.isOpened():
        print("Ошибка при открытии видеофайла.")

    data_frame_video = list()
    while True:
        ret, frame = video_array.read()
        if not ret:
            break
        res = frame_to_cord(frame)

        if len(res) != 18: print("Error!")
        data_frame_video.append(res)

    return data_frame_video


def frame_to_cord(frame):
    num_block_access_cords = [8, 6, 5, 4, 1, 2, 3, 7, 18, 20, 22, 21, 19, 17]
    num_access_cords = list(filter(lambda x: x not in num_block_access_cords, list(range(32))))

    results = pose.process(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))

    cord = list()
    for i in num_access_cords:
        try:
            x = results.pose_landmarks.landmark[mp_pose.PoseLandmark(i).value].x
            y = results.pose_landmarks.landmark[mp_pose.PoseLandmark(i).value].y

            if x == None or y == None:
                cord.append([0, 0])
            else:
                cord.append([x, y])
        except:
            cord.append([0, 0])

    return cord


class Data(BaseModel):
    video: bytes


app = FastAPI()
origins = ["*"]
app.add_middleware(CORSMiddleware, allow_origins=origins, allow_credentials=True, allow_methods=["*"],
                   allow_headers=["*"])


# Конь отправляет видео и получает видео+отзыв
@app.post("/get_review")
def get_review(data: Data):
    # print("data")
    # decoding and saving video
    fh = open("input.mp4", "wb")
    fh.write(base53.b64decode(data.video))
    fh.close()

    # PROCESS MLAIRECOGNIZER
    # Предсказание кадров с позами_ обработка
    path = "input.mp4"
    cap = cv2.VideoCapture(path)
    x_data = video_now(cap)

    cap.release()
    coords = []
    for pic in x_data:
        dots = []
        for i in range(18):
            dots.append(pic[i][0])
            dots.append(pic[i][1])
        coords.append(dots)

    ans = list(model_for_poses.predict_proba(coords))

    frames_of_poses = [0, 0, 0, 0, 0, 0, 0, 0]
    sure_of_poses = [0, 0, 0, 0, 0, 0, 0, 0]

    for i in range(len(ans)):
        for num in range(8):
            if (ans[i][num] > sure_of_poses[num]):
                sure_of_poses[num] = ans[i][num]
                frames_of_poses[num] = i
    # Сохранение кадров с позами для аналитики
    names_of_pose_photos = ["1.jpg", "2.jpg", "3.jpg", "4.jpg", "5.jpg", "7.jpg", "8.jpg", "10.jpg"]

    for i in range(len(frames_of_poses)):
        cap1 = cv2.VideoCapture(path)
        counter = 0
        while True:
            ret, frame = cap1.read()
            if not ret:
                break
            if frames_of_poses[i] == counter:
                Image.fromarray(cv2.cvtColor(frame.astype('uint8'), cv2.COLOR_BGR2RGB)).save(names_of_pose_photos[i])
                break
            counter += 1
        cap1.release()

    # Определение ракурса
    count = 0
    class_of_racurs = model_racurs.predict([coords[frames_of_poses[0]]])[0]
    racurs = ["The camera shoots from the face", "The camera shoots on the left", "The camera shoots on the right"][
        class_of_racurs]

    # Определение ошибки с отодвиганием головы
    p1 = np.asarray(Image.open("1.jpg"))
    p4 = np.asarray(Image.open("4.jpg"))

    coords_of_p1 = pic_to_cord(p1)
    coords_of_p4 = pic_to_cord(p4)

    im_p1 = Image.open("1.jpg")
    im_p4 = Image.open("4.jpg")

    width, height = im_p4.size
    draw = ImageDraw.Draw(im_p4)

    x1_1 = coords_of_p1[0][0] * width
    y1_1 = coords_of_p1[0][1] * height

    x1_4 = coords_of_p4[0][0] * width
    y1_4 = coords_of_p4[0][1] * height

    distance_between_dots = distance(x1_1, y1_1, x1_4, y1_4)

    flag_of_mistake = 0
    if (distance_between_dots >= max(width, height) * 0.05):
        draw = ImageDraw.Draw(im_p4)
        draw.line((x1_1, y1_1, x1_4, y1_4), fill='green', width=3)
        im_p4.save("Head movement.jpg")
        flag_of_mistake = 1

    # writing video result to 'result.mp4' and creating dict with comments/review
    review = {"comment": "Everything is fine", "errors": "", "racurs": racurs}

    if (flag_of_mistake):
        review["errors"] = "Head movement"
        review["comment"] = f"Your head has moved {round(distance_between_dots)} pixels"

    # Я не меняю выходящее видео!
    # fh = open("result.mp4", "rb")
    # video_res = fh.read()
    # fh.close()
    # review['video'] = video_res

    return str(review)
