import cv2
import os
import numpy as np

# Existing subject names
subjects = ["", "Mark Zuckerberg", "Pao Yin", "Jack Ma"]

def predict_detect(img):
	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	face_cascade = cv2.CascadeClassifier('/home/pi/opencv/opencv/data/lbpcascades/lbpcascade_frontalface.xml')
	faces = face_cascade.detectMultiScale(gray, scaleFactor = 1.2, minNeighbors = 15)
	
	if (len(faces) == 0):
		return None, None
	
	(x, y, w, h) = faces[0]
	return gray[y:y+w, x:x+h], faces[0]

# Train
def prepare_training_data(data_folder_path):
	dirs = os.listdir(data_folder_path)
	faces = []
	labels = []
	
	for dir_name in dirs:
		if not dir_name.startswith("s"):
			continue;
		
		label = int(dir_name.replace("s", ""))
		subject_dir_path = data_folder_path + "/" + dir_name
		subject_img_names = os.listdir(subject_dir_path)
		
		for img_name in subject_img_names:
			if img_name.startswith("."):
				continue;
			
			img_path = subject_dir_path + "/" + img_name
			img = cv2.imread(img_path)
			
			face, box = predict_detect(img)
			
			if face is not None:
				faces.append(face)
				labels.append(label)
				
	return faces, labels

# Train on pre-loaded images
faces, labels = prepare_training_data("training-data")
face_recognizer = cv2.face.LBPHFaceRecognizer_create()
face_recognizer.train(faces, np.array(labels))
face_recognizer.write("face_model.xml")
