import numpy as np
import math
from os.path import dirname, join
from PIL import Image, ImageDraw
from joblib import dump, load
import base64

def distance(x1, y1, x2, y2):
    return math.sqrt((x2-x1)**2 + (y2-y1)**2)

def pose_codr_model(data_frame_cord_pose):
    if len(data_frame_cord_pose) == 0:
        return [0,0,0,0,0,0,0,0]
    data_frame_cord_pose = np.array(data_frame_cord_pose)
    
    data_p_pose = list()
    for frame_all_cord_pose in frame_all_cord_pose:
        frame_all_cord_pose = np.array(frame_all_cord_pose)[:-1]
        block_access_cords = [1, 2, 3, 4, 5, 6, 7, 8, 18, 20, 22, 21, 19, 17]
        frame_access_cord_pose = np.delete(frame_all_cord_pose, block_access_cords, axis=0)
        frame_access_cord_pose = np.concatenate(frame_access_cord_pose)
        frame_access_cord_pose[frame_access_cord_pose == None] = 0
        data_p_pose.append(frame_access_cord_pose)
        
    with open(join(dirname(__file__), "model_pose.joblib"), "rb") as f:
        model_pose = load(f)

    answer_pose = model_pose.predict_proba(data_p_pose)

    frames_of_poses = [0, 0, 0, 0, 0, 0, 0, 0]
    sure_of_poses = [0, 0, 0, 0, 0, 0, 0, 0]

    for i in range(len(answer_pose)):
        for num in range(8):
            if(answer_pose[i][num] > sure_of_poses[num]):
                sure_of_poses[num] = answer_pose[i][num]
                frames_of_poses[num] = i

    return frames_of_poses


def pose_bytearray_model(data_bytearray_pose, frames_of_poses):
    img_p1, cord_p1 = Image.fromarray(np.frombuffer(data_bytearray_pose[0], dtype=np.uint8)), frames_of_poses[0]
    img_p4, cord_p4 = Image.fromarray(np.frombuffer(data_bytearray_pose[3], dtype=np.uint8)), frames_of_poses[3]

    width, height = img_p4.size
    draw = ImageDraw.Draw(img_p4)

    x1_1, y1_1 = cord_p1[0][0] * width, cord_p1[0][1] * height
    x1_4, y1_4 = cord_p4[0][0] * width, cord_p4[0][1] * height
    
    distance_between_dots = distance(x1_1, y1_1, x1_4, y1_4)

    if (distance_between_dots >= width*0.1):
        draw = ImageDraw.Draw(img_p4)
        draw.line((x1_1, y1_1,x1_4,y1_4), fill='green', width=3)

        img_P4 = img_p4.tobytes()

        base64_string = base64.b64encode(img_P4).decode('utf-8')
        return base64_string
    return ''