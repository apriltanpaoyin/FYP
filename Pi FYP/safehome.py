'''
	This is the code for the main processing. It performs facial detection, facial recognition, 
	and motion detection. Uploads, notifications and the alarm is triggered in this file as well.
	Uploads uses the g_drive file, notifications uses the notify file. Alarms are played as a 
	thread using the VLC library.
'''
import cv2
import argparse
import datetime
import time
import imutils
import os
import numpy as np
import sys
import vlc
import json
from threading import Thread
import g_drive
import notify

# Define classifier
lbp = cv2.CascadeClassifier("/home/pi/opencv/opencv/data/lbpcascades/lbpcascade_frontalface.xml");
# Existing subject names
subjects = ["", "Pao Yin", "Kieran"]
prevName = ""
faceCnt = 0

# Argument parser
ap = argparse.ArgumentParser()
ap.add_argument("-a", "--min-area", type = int, default = 700, help = "minimum area size")
ap.add_argument("--conf", default = "/var/www/html/config.json")
args = vars(ap.parse_args())

# Initialize sound file
player = vlc.MediaPlayer("file:///home/pi/FYP/sounds/alarm.mp3")

# Var for uploads
motionFrames = 0
lastUpload = datetime.datetime.utcnow()

# Google Drive Authentication
g_drive.authentication()
gauth = g_drive.get_gauth()
drive = g_drive.get_drive()

# Detects faces in an image passed as a parameter using lbp cascade
def predict_detect(img):
	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	face_cascade = cv2.CascadeClassifier('/home/pi/opencv/opencv/data/lbpcascades/lbpcascade_frontalface.xml')
	faces = face_cascade.detectMultiScale(gray, scaleFactor = 1.2, minNeighbors = 15)
	
	if (len(faces) == 0):
		return None, None
	
	(x, y, w, h) = faces[0]
	return gray[y:y+w, x:x+h], faces[0]

# Make predictions on a detected face
def predict(img):
	global prevName, faceCnt
	face, box = predict_detect(img)

	if face is None:
		faceCnt = 0
		return None
	
	face_recognizer = cv2.face.LBPHFaceRecognizer_create()
	# Load trained model
	face_recognizer.read("face_model.xml")
	label, confidence = face_recognizer.predict(face)
	name = subjects[label]

	# Draw box around predicted face
	(x, y, w, h) = box
	
	# Lower confidence means it is more accurate. Prevents false positives.
	if confidence < 120:
		if name == prevName:
			faceCnt += 1
		else:
			prevName = name
			faceCnt = 0
			
		# Put the predicted name
		cv2.putText(img, name, (box[0], box[1]+h+5), cv2.FONT_HERSHEY_PLAIN, 1.5, (0, 255, 0), 2)
	
	return img
	
def alarm_sound():
	# Plays alarm for a set time
	player.play()
	time.sleep(3)
	player.stop()

def upload_image():
	global lastUpload, motionFrames
	currentTime = datetime.datetime.utcnow()
	
	# Counts motion frame 5 seconds after upload. Prevents jumpy vid
	if (currentTime - lastUpload).seconds >= 5:
		# Count number of frames with motion
		motionFrames += 1
		
		# If consistent motion
		if motionFrames >= 10:
			# Write frame to temporary image
			temp = g_drive.get_tmpimg()
			cv2.imwrite(temp.path, I)
			
			# Upload to drive
			g_drive.uploader(currentTime.__str__(), temp)
			
			# Wait for upload to finish then send notification
			if (currentTime - lastUpload).seconds >= 20:
				notify.notify()

			# Check to see if a face has been recognized before alarm
			if conf["alarm_set"] and faceCnt < 5:
				try:
					thread = Thread(target = alarm_sound, args=())
					thread.start()
					thread.join()
				except RuntimeError as e:
					print(e)
			elif faceCnt > 5:
				# If confirmed user, set alarm to off.
				data = {"alarm_set": False}
				with open(args["conf"], 'w') as c:
					json.dump(data, c)
			temp.cleanup()
			lastUpload = currentTime
			motionFrames = 0          

# Read from webcam
camera = cv2.VideoCapture(0)

# Initialize first frame
first = None

while True:
	# Initialize conf file
	conf = json.load(open(args["conf"]))
	(grabbed, I) = camera.read()
	text = "No motion detected"
	
	# If can't get frame
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
	thresh = cv2.threshold(Idelta, 30, 255, cv2.THRESH_BINARY)[1]
	
	# Dilate to fill holes, then find contours
	thresh = cv2.dilate(thresh, None, iterations = 2)
	(_, cnts, _) = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
	
	for c in cnts:
		# Ignore contours that are too small
		if cv2.contourArea(c) < args["min_area"]:
			continue
		
		text = "Motion Detected"
	
	timestmp = datetime.datetime.now().strftime("%A %d %B %Y %I:%M:%S%p")
	cv2.putText(I, "Status: {}".format(text), (10, 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)
	cv2.putText(I, timestmp, (10, I.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 0, 255), 1)
	
	if text == "Motion Detected":
		# Predict the first detected face.
		predicted = predict(I)
		upload_image()
	else:
		motion = 0
	
	# Show image (Demo purposes only)
	cv2.imshow("Security Feed", I)
	key = cv2.waitKey(1)
	
	if key == ord("q"):
		break

camera.release()
cv2.destroyAllWindows()
