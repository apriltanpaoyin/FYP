import cv2
from matplotlib import pyplot as plt

I = cv2.imread("group.jpg")
haar = cv2.CascadeClassifier("/home/pi/opencv-3.2.0/data/haarcascades/haarcascade_frontalface_alt.xml");

def detect_faces(haar, I, scaleFactor = 1.1):
	G = cv2.cvtColor(I, cv2.COLOR_BGR2GRAY)
	faces = haar.detectMultiScale(G, scaleFactor = scaleFactor, minNeighbors = 5)
	
	print("Faces found: {}".format(len(faces)))
	
	for (x, y, w, h) in faces:
		cv2.rectangle(img = I, pt1 = (x, y), pt2 = (x+w, y+h), color = (255, 0, 0), thickness = 2)
		
	return I

faces = detect_faces(haar, I)
cv2.imshow("faces", faces)
key = cv2.waitKey(0)
