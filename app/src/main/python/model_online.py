import numpy as np
from os.path import dirname, join
from PIL import Image, ImageDraw
from joblib import dump, load


def check_person(frame_all_cord_pose):
    frame_all_cord_pose = np.array(frame_all_cord_pose)[:-1]
    block_access_cords = [1, 2, 3, 4, 5, 6, 7, 8, 18, 20, 22, 21, 19, 17]
    frame_access_cord_pose = np.delete(frame_all_cord_pose, block_access_cords, axis=0)
    frame_access_cord_pose = np.concatenate(frame_access_cord_pose)
    

    if len(frame_access_cord_pose[frame_access_cord_pose == None]) >= 6:
        return False
    return True

def racurs_model(frame_all_cord_pose):
    frame_all_cord_pose = np.array(frame_all_cord_pose)[:-1]
    block_access_cords = [1, 2, 3, 4, 5, 6, 7, 8, 18, 20, 22, 21, 19, 17]
    frame_access_cord_pose = np.delete(frame_all_cord_pose, block_access_cords, axis=0)
    frame_access_cord_pose = np.concatenate(frame_access_cord_pose)
    frame_access_cord_pose[frame_access_cord_pose == None] = 0

    with open(join(dirname(__file__), "model_racurs.joblib"), "rb") as f:
        model_racurs = load(f)

    answer_racurs = model_racurs.predict([frame_access_cord_pose])

    return int(answer_racurs)

def video_model(frame_all_cord_pose):
    frame_all_cord_pose = np.array(frame_all_cord_pose)[:-1]
    block_access_cords = [1, 2, 3, 4, 5, 6, 7, 8, 18, 20, 22, 21, 19, 17]
    frame_access_cord_pose = np.delete(frame_all_cord_pose, block_access_cords, axis=0)
    frame_access_cord_pose = np.concatenate(frame_access_cord_pose)
    frame_access_cord_pose[frame_access_cord_pose == None] = 0

    with open(join(dirname(__file__), "model_video.joblib"), "rb") as f:
        model_video = load(f)
    
    answer_video = np.argmax(model_video.predict_proba([frame_access_cord_pose]))

    return int(answer_video)