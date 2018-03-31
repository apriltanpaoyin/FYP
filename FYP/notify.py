from pyfcm import FCMNotification
import g_drive

def notify():
	push_service = FCMNotification(api_key="AAAA4p4edZs:APA91bGYTAl82ZRTtzX9bOfzaklxd1-oRcV2aGrKMhiOvKlBO349PsGmGbPhI995uAU7LDj7uehTwv3-qAAq5jV4zLm0LHoWZ2mE512UlcPyCju-QTnNxTwuEQvrm_kJqfSdYpx872zW")
	registration_id = "dBpX7FeBbZg:APA91bHlDiuBccvuV_PH_Ht_rrMRIk7HWbm9mN0o-75wZnJRtYcmUuYF6LSua5UFHkahhHbv6nZaReUMI6jLaMMixC7T8fKCy_ciryWWvlrBCrY1W8FKJiFzSFP1emtoH_Dls8B71mVa"
	message_title = "Motion Detected"
	message_body = "Image added"
	result = push_service.notify_single_device(registration_id=registration_id, message_title=message_title, message_body=message_body)
	return result
notify()
