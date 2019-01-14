import numpy as np
from keras.models import load_model

def predict(t):
    model = load_model('data/my_model.h5')
    testPredict = model.predict(t[np.newaxis, np.newaxis, :])
    return testPredict