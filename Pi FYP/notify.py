'''
	This code is used to send notifications to the registered user when an upload occurs.
'''
from pyfcm import FCMNotification
import g_drive

def notify():
	push_service = FCMNotification(api_key="AAAA4p4edZs:APA91bGYTAl82ZRTtzX9bOfzaklxd1-oRcV2aGrKMhiOvKlBO349PsGmGbPhI995uAU7LDj7uehTwv3-qAAq5jV4zLm0LHoWZ2mE512UlcPyCju-QTnNxTwuEQvrm_kJqfSdYpx872zW")
	registration_id = "c8PpIUNChPM:APA91bGNigZ4yRrhwSpkBYfArOF1UKwITkeXQ8x8UokUbz4wx_i-OWLXCXN5PnaAJ8qN_ULcBusx9pbzAbe79c5ucL29fiXgS-QrY62_-gJ2XMlPlUIxi17uFbUd3eRIBn6gI9tpUz1j"
	message_title = "Motion Detected"
	message_body = "Image added"
	result = push_service.notify_single_device(registration_id=registration_id, message_title=message_title, message_body=message_body)
	return result
notify()
