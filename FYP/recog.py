import cv2
import argparse
import datetime
import time
import imutils
import os
import numpy as np

# Define classifier
lbp = cv2.CascadeClassifier("/home/pi/opencv/opencv/data/lbpcascades/lbpcascade_frontalface.xml");
# Existing subject names
subjects = ["", "Mark Zuckerberg", "Pao Yin", "Jack Ma"]

def predict_detect(img):
	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	face_cascade = cv2.CascadeClassifier('/home/pi/opencv/opencv/data/lbpcascades/lbpcascade_frontalface.xml')
	faces = face_cascade.detectMultiScale(gray, scaleFactor = 1.2, minNeighbors = 5)
	
	if (len(faces) == 0):
		return None, None
	
	(x, y, w, h) = faces[0]
	return gray[y:y+w, x:x+h], faces[0]

def detect_faces(lbp, I, scaleFactor = 1.1):
	G = cv2.cvtColor(I, cv2.COLOR_BGR2GRAY)
	faces = lbp.detectMultiScale(G, scaleFactor = scaleFactor, minNeighbors = 5)
	
	for (x, y, w, h) in faces:
		cv2.rectangle(I, (x, y), (x+w, y+h), (255, 0, 0), 2)
		cv2.putText(I, "Face", (x, y), cv2.FONT_HERSHEY_PLAIN, 1.5, (255, 0, 0), 2)
		
	return I

# Make predictions
def predict(img):
	face, box = predict_detect(img)

	if face is None:
		return None

	label, confidence = face_recognizer.predict(face)
	name = subjects[label]
	# Draw box around predicted face
	(x, y, w, h) = box
	#cv2.rectangle(img, (x, y), (x+w, y+h), (0, 0, 255), 2)
	# Put the predicted name
	cv2.putText(img, name, (box[0], box[1]+h+5), cv2.FONT_HERSHEY_PLAIN, 1.5, (0, 255, 0), 2)
	
	return img

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

#Argument parser
ap = argparse.ArgumentParser()
ap.add_argument("-a", "--min-area", type = int, default = 700, help = "minimum area size")
args = vars(ap.parse_args())

#Read from webcam
camera = cv2.VideoCapture(0)

#Initialize first frame
first = None

while True:
	(grabbed, I) = camera.read()
	text = "No motion detected"
	
	#If can't get frame
	if not grabbed:
		break
	
	G = cv2.cvtColor(I, cv2.COLOR_BGR2GRAY)
	G = cv2.GaussianBlur(G, (21, 21), 0)
	
	# If no first frame, use G as first
	if first is None:
		first = G
		continue
	
	# Get difference btwn current frame & first frame
	Idelta = cv2.absdiff(first, G)
	thresh = cv2.threshold(Idelta, 25, 255, cv2.THRESH_BINARY)[1]
	
	# Dilate to fill holes, then find contours
	thresh = cv2.dilate(thresh, None, iterations = 2)
	(_, cnts, _) = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
	
	for c in cnts:
		# Ignore contours that are too small
		if cv2.contourArea(c) < args["min_area"]:
			continue
			
		# Boundary for box
		(x, y, w, h) = cv2.boundingRect(c)
		cv2.rectangle(I, (x, y), (x+w, y+h), (0, 255, 0), 2)
		text = "Motion Detected"
	
	# Detect faces
	faces = detect_faces(lbp, I)

	# Predict the first detected face
	predicted = predict(I)
	
	cv2.putText(I, "Status: {}".format(text), (10, 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)
	cv2.putText(I, datetime.datetime.now().strftime("%A %d %B %Y %I:%M:%S%p"), (10, I.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 0, 255), 1)
	
	#Show image
	cv2.imshow("Security Feed", I)
	key = cv2.waitKey(1)
	
	if key == ord("q"):
		break

camera.release()
cv2.destroyAllWindows()
