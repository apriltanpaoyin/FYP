from pyfcm import FCMNotification
import g_drive

def notify():
	push_service = FCMNotification(api_key="AAAA4p4edZs:APA91bGYTAl82ZRTtzX9bOfzaklxd1-oRcV2aGrKMhiOvKlBO349PsGmGbPhI995uAU7LDj7uehTwv3-qAAq5jV4zLm0LHoWZ2mE512UlcPyCju-QTnNxTwuEQvrm_kJqfSdYpx872zW")
	registration_id = "cgKHWckdbRI:APA91bFYw5K-BIsUMsuTWEZRkEjtNmIuDj23wiUHsioxiCq2GcCOzmVU68aXCRXIm3LZxgWP4yTFYOTY_jwLuL6BRFaT_UFmbPaaLLy8qOSOsoUmh8CrJP0YU4MTIFiS-sXi6D8NsDZs"
	message_title = "Motion Detected"
	message_body = "Image added"
	result = push_service.notify_single_device(registration_id=registration_id, message_title=message_title, message_body=message_body)
	return result
notify()
