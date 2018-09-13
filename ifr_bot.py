from chatterbot import ChatBot
import pyttsx3
import speech_recognition as sr
import argparse
import numpy as np
from cv2 import *
import matplotlib.pyplot as plt
from mxnet import nd, image
from mxnet.gluon.data.vision import transforms
from gluoncv.model_zoo import get_model


    
def recognize_speech_from_mic(recognizer, microphone): 
    if not isinstance(recognizer, sr.Recognizer):
        raise TypeError("`recognizer` must be `Recognizer` instance")

    if not isinstance(microphone, sr.Microphone):
        raise TypeError("`microphone` must be `Microphone` instance")

    with microphone as source:
        recognizer.adjust_for_ambient_noise(source)
        audio = recognizer.listen(source)

    response = {
        "success": True,
        "error": None,
        "transcription": None
    }
    try:
        response["transcription"] = recognizer.recognize_google(audio)
    except sr.RequestError:
        # API was unreachable or unresponsive
        response["success"] = False
        response["error"] = "API unavailable"
    except sr.UnknownValueError:
        # speech was unintelligible
        response["error"] = "Unable to recognize speech"
    
    return response
def detect_image():
    cam = VideoCapture(1)   # 0 -> index of camera
    s, img = cam.read()
    imwrite("shyam.jpg",img)
    cam.release()
    img = image.imread("shyam.jpg")
    # Transform
    print("start transform")
 
    transform_fn = transforms.Compose([
        transforms.Resize(256),
        transforms.CenterCrop(224),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
    ])

    img = transform_fn(img)
    pred = net(img.expand_dims(0))

    topK = 5
    ind = nd.topk(pred, k=topK)[0].astype('int')
    print('The input picture is classified to be')
    for i in range(topK):
        engine.say(class_names[ind[i].asscalar()])
        print('\t[%s], with probability %.3f.'%
              (class_names[ind[i].asscalar()], nd.softmax(pred)[0][ind[i]].asscalar()))
        engine.runAndWait()

if __name__ == "__main__":
    engine=pyttsx3.init()
    engine.say("started setup")
    engine.runAndWait()
    print("started initializing")
    engine.say("started training")
    engine.runAndWait()
    chatbot = ChatBot('shyam',trainer='chatterbot.trainers.ChatterBotCorpusTrainer')
    chatbot.train('chatterbot.corpus.english')
    engine.say("completed training")
    engine.runAndWait()
    recognizer = sr.Recognizer()
    microphone = sr.Microphone()
    engine.say("loading model")
    engine.runAndWait()
    classes = 1000
    with open('imagenet_labels.txt', 'r') as f:
        class_names = [l.strip('\n') for l in f.readlines()]
        
    # Load Model
    model_name = "resnet50_v2"
    pretrained = True
    '''if opt.saved_params == '' else False'''
    kwargs = {'classes': classes, 'pretrained': pretrained}
    net = get_model(model_name, **kwargs)
    if not pretrained:
        print("notpretrained")
        pass
    engine.say("hello world")
    engine.runAndWait()
    engine.say("i am shyam")
    engine.runAndWait()
    while True:
        while True:
            
            guess = recognize_speech_from_mic(recognizer, microphone)
            if guess["transcription"]:
                break
            if not guess["success"]:
                break
            engine.say("I didn't catch that. What did you say?\n")
            engine.runAndWait
        if guess["error"]:
            print("ERROR: {}".format(guess["error"]))
            break
        print(guess["transcription"])
        if(guess["transcription"]=="detect image"):
            detect_image()
        else:
            response = chatbot.get_response(guess["transcription"])
            engine.say(response)
            engine.runAndWait()

