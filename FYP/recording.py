import cv2
import argparse
import datetime
import time
import imutils
import os
import numpy as np
import sys

import training

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

	label, confidence = training.get_recognizer().predict(face)
	name = subjects[label]
	# Draw box around predicted face
	(x, y, w, h) = box
	#cv2.rectangle(img, (x, y), (x+w, y+h), (0, 0, 255), 2)
	# Put the predicted name
	cv2.putText(img, name, (box[0], box[1]+h+5), cv2.FONT_HERSHEY_PLAIN, 1.5, (0, 255, 0), 2)
	
	return img
	
def upload_images():
	currentTime = datetime.datetime.utcnow()
	
	if (currentTime - lastUpload).seconds >= 15:
		# Count number of frames with motion
		motionFrames += 1
		
		# If consistent motion
		if motionFrames >= 8:
			# Write frame to temporary image
			temp.g_drive.get_ti()
			cv2.imwrite(temp.path, I)
			
			# Upload to drive
			g_drive.upload(timestmp)

# Define classifier
lbp = cv2.CascadeClassifier("/home/pi/opencv/opencv/data/lbpcascades/lbpcascade_frontalface.xml");

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
		motionFrames = 0
		lastUpload = datetime.datetime.utcnow()
		continue
	
	# Get difference btwn current frame & first frame
	Idelta = cv2.absdiff(first, G)
	thresh = cv2.threshold(Idelta, 30, 255, cv2.THRESH_BINARY)[1]
	
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
	
	timestmp = datetime.datetime.now().strftime("%A %d %B %Y %I:%M:%S%p")
	cv2.putText(I, "Status: {}".format(text), (10, 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)
	cv2.putText(I, timestmp, (10, I.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 0, 255), 1)
	
	# If movement, upload to drive
	if text == "Motion Detected":
		upload_image()
	else:
		motion = 0
	
	# Write to stdout
	Istr = np.array2string(I)
	sys.stdout.write(Istr)
	
	# Show image (Demo purposes only)
	cv2.imshow("Security Feed", I)
	key = cv2.waitKey(1)
	
	if key == ord("q"):
		break

camera.release()
cv2.destroyAllWindows()
