import cv2
import numpy as np
from matplotlib import pyplot as plt
from matplotlib import image as image

camera = cv2.VideoCapture(0)
(grabbed, I) = camera.read()

while grabbed:
	(grabbed, I) = camera.read()
	cv2.imshow("image", I)
	key = cv2.waitKey(1)
	
	if key == ord("q"):
		break
