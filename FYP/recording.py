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

#from training import get_recognizer
import g_drive

# Define classifier
lbp = cv2.CascadeClassifier("/home/pi/opencv/opencv/data/lbpcascades/lbpcascade_frontalface.xml");
# Existing subject names
subjects = ["", "Pao Yin"]

# Argument parser
ap = argparse.ArgumentParser()
ap.add_argument("-a", "--min-area", type = int, default = 700, help = "minimum area size")
ap.add_argument("--conf", default = "conf.json")
args = vars(ap.parse_args())

# Initialize conf file
conf = json.load(open(args["conf"]))

# Initialize sound file
player = vlc.MediaPlayer("file:///home/pi/FYP/sounds/alarm.mp3")

# Var for uploads
motionFrames = 0
lastUpload = datetime.datetime.utcnow()

# Face detection
def detect_faces(lbp, I, scaleFactor = 1.1):
	G = cv2.cvtColor(I, cv2.COLOR_BGR2GRAY)
	faces = lbp.detectMultiScale(G, scaleFactor = scaleFactor, minNeighbors = 5)
	
	for (x, y, w, h) in faces:
		cv2.rectangle(I, (x, y), (x+w, y+h), (255, 0, 0), 2)
		cv2.putText(I, "Face", (x, y), cv2.FONT_HERSHEY_PLAIN, 1.5, (255, 0, 0), 2)
		
	return I

# Used for face recog
def predict_detect(img):
	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	face_cascade = cv2.CascadeClassifier('/home/pi/opencv/opencv/data/lbpcascades/lbpcascade_frontalface.xml')
	faces = face_cascade.detectMultiScale(gray, scaleFactor = 1.2, minNeighbors = 15)
	
	if (len(faces) == 0):
		return None, None
	
	(x, y, w, h) = faces[0]
	return gray[y:y+w, x:x+h], faces[0]

# Make predictions
def predict(img):
	face, box = predict_detect(img)

	if face is None:
		return None
	
	face_recognizer = cv2.face.LBPHFaceRecognizer_create()
	# Load trained model
	face_recognizer.read("face_model.xml")
	label, confidence = face_recognizer.predict(face)
	name = subjects[label]
	
	# Draw box around predicted face
	(x, y, w, h) = box
	
	print (confidence)
	if confidence < 120:
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
	
	if (currentTime - lastUpload).seconds >= 5:
		# Count number of frames with motion
		motionFrames += 1
		
		# If consistent motion
		if motionFrames >= 8:
			# Write frame to temporary image
			temp = g_drive.get_tmpimg()
			cv2.imwrite(temp.path, I)
			
			# Upload to drive
			g_drive.uploader(currentTime.__str__(), temp)
			
			# Wait for upload to finish then send notification
			if (currentTime - lastUpload).seconds >= 10:
				notify.notify()
				
				# CHECK TO SEE IF A FACE HAS BEEN RECOGNIZED BEFORE PLAYING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				if conf["alarmSet"]:
					try:
						thread = Thread(target = alarm_sound, args=())
						thread.start()
						thread.join()
					except:
						print ("Thread didn't work.")
			temp.cleanup()
			lastUpload = currentTime
			motionFrames = 0                    

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
	#if text == "Motion Detected":
		#upload_image()
	#else:
		#motion = 0
	
	# Write to stdout
	#Istr = np.array2string(I)
	#sys.stdout.write(Istr)
	
	# Show image (Demo purposes only)
	cv2.imshow("Security Feed", I)
	key = cv2.waitKey(1)
	
	if key == ord("q"):
		break

camera.release()
cv2.destroyAllWindows()
