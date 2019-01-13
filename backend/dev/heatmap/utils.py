import pandas as pd
from datetime import datetime, timedelta
from . import models
import math

def get_excel_data():
    file = 'data/SyntheticData.xlsx'
    xl = pd.ExcelFile(file)
    print(xl.sheet_names)
    df1 = xl.parse('Sheet1')
    df[(df.index == 'x') & (df.col1 == 'a')]
    return df1

def get_csv_data(special_day):
    file = 'data/' + special_day + '.csv'
    x1 = pd.read_csv(file, header=None)
    data = []
    getDate = datetime.now()
    minute = getDate.minute
    print(minute)
    while minute % 10 != 0:
        minute = minute + 1
        if minute == 60:
            minute = 0
            getDate = getDate + timedelta(minutes=60)
    
    
    if minute == 0:
        time = getDate.strftime("%H:") + "00"
    else:
        time = getDate.strftime("%H:") + str(minute)
    
    print("Hit Time: ", time)
    for index, row in x1.iterrows():
        block_data = row.values.tolist()
        if block_data[0] in time:
            return block_data[2:]
    return []

def find_block(lat, lon):
    print ("New: ", lat, " ", lon)
    block_number = -1
    full = models.Block.objects.all()
    min_dist = 1000000000.000
    for obj in full:
        dist = math.sqrt(pow(abs(obj.latitude-lat),2) + pow(abs(obj.longitude-lon),2))
        # print ("Distance to " + obj.name + str(dist))
        if dist < min_dist:
            block_number = obj
            min_dist = dist
    
    return block_number

def get_driver_stats():
    full = models.Block.objects.all()
    stats = []
    for obj in full:
        if obj.resources <= 0:
            obj.resources = 1
        stats.append(obj.resources)
    return stats

def get_all_lat_lon():
    full = models.Block.objects.all()
    ret = []
    for obj in full:
        ret.append(obj.latitude)
        ret.append(obj.longitude)
    return ret

def shuffle_data():
    full = models.Block.objects.all()
    ret = []
    for i in range(0, 98):
        models.DriverLocation.objects.create()
    for obj in full:
        ret.append(obj.latitude)
        ret.append(obj.longitude)
    return ret

def print_blocks_data():
    full = models.Block.objects.all()
    for obj in full:
        print (obj.__dict__)