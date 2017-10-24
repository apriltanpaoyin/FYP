import cv2
import argparse
import datetime
import time
import imutils

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
	
	#If no first frame, use G as first
	if first is None:
		first = G
		continue
	
	#Get difference btwn current frame & first frame
	Idelta = cv2.absdiff(first, G)
	thresh = cv2.threshold(Idelta, 25, 255, cv2.THRESH_BINARY)[1]
	
	#Dilate to fill holes, then find contours
	thresh = cv2.dilate(thresh, None, iterations = 2)
	(_, cnts, _) = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
	
	for c in cnts:
		#Ignore contours that are too small
		if cv2.contourArea(c) < args["min_area"]:
			continue
			
		#Boundary for box
		(x, y, w, h) = cv2.boundingRect(c)
		cv2.rectangle(I, (x, y), (x+w, y+h), (0, 255, 0), 2)
		text = "Motion Detected"
	
	cv2.putText(I, "Status: {}".format(text), (10, 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)
	cv2.putText(I, datetime.datetime.now().strftime("%A %d %B %Y %I:%M:%S%p"), (10, I.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 0, 255), 1)
	
	#Show image
	cv2.imshow("Security Feed", I)
	key = cv2.waitKey(1)
	
	if key == ord("q"):
		break

camera.release()
cv2.destroyAllWindows()
