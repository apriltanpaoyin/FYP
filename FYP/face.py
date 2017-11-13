import cv2
import os
import numpy as np

subjects = ["", "Jack Ma", "Mark Zuckerberg", "Pao Yin"]

def detect_face(img):
	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	face_cascade = cv2.CascadeClassifier('/home/pi/opencv/opencv/data/lbpcascades/lbpcascade_frontalface.xml')
	
	faces = face_cascade.detectMultiScale(gray, scaleFactor = 1.2, minNeighbors = 5)
	
	if (len(faces) == 0):
		return None, None
	
	(x, y, w, h) = faces[0]
	return gray[y:y+w, x:x+h], faces[0]

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
			
			face, box = detect_face(img)
			
			if face is not None:
				faces.append(face)
				labels.append(label)
				
	return faces, labels

# Train on images
faces, labels = prepare_training_data("training-data")
face_recognizer = cv2.face.LBPHFaceRecognizer_create()
face_recognizer.train(faces, np.array(labels))

def draw_box(img, box):
	(x, y, w, h) = box
	cv2.rectangle(img, (x, y), (x+w, y+h), (255, 0, 0), 2)
	
def draw_txt(img, txt, x, y):
	cv2.putText(img, txt, (x, y), cv2.FONT_HERSHEY_PLAIN, 1.5, (255, 0, 0), 2)
	
def predict(img):
	face, box = detect_face(img)
	
	label, confidence = face_recognizer.predict(face)
	label_txt = subjects[label]
	
	draw_box(img, box)
	draw_txt(img, label_txt, box[0], box[1]-5)
	return img

# Predict based on image
I = cv2.imread("test-data/jack1.jpg")
I2 = cv2.imread("test-data/zuck1.jpg")
I3 = cv2.imread("test-data/myface.jpg")

pred1 = predict(I)
pred2 = predict(I2)
pred3 = predict(I3)

#cv2.imshow(subjects[1], pred1)
#cv2.imshow(subjects[2], pred2)
cv2.imshow(subjects[3], pred3)
key = cv2.waitKey(0)
cv2.destroyAllWindows()
