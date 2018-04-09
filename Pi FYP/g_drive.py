from tempimage import TempImage
from pydrive.auth import GoogleAuth
from pydrive.drive import GoogleDrive
import datetime

# Initialize temp image
temp = TempImage()

# Google Drive authentication
gauth = GoogleAuth()
drive = GoogleDrive(gauth)

def get_gauth():
	return gauth
	
def get_drive():
	return drive
	
def get_tmpimg():
	return temp

def authentication():
	try:
		gauth.LoadCredentialsFile("credentials.txt")
		
		if gauth.credentials is None:
			gauth.LocalWebserverAuth()
		elif gauth.access_token_expired:
			gauth.Refresh()
		else:
			gauth.Authorize()
		
		gauth.SaveCredentialsFile("credentials.txt")
	except RuntimeError as e:
		print(e)
		
def uploader(timestamp, temp):
	file = drive.CreateFile()
	file.SetContentFile(temp.path)
	file['title'] = timestamp
	file.Upload()
	print('Created file %s' % (file['title']))
