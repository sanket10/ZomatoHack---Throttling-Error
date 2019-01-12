from django.db import models
from django.contrib.postgres.fields import ArrayField

class Orders(models.Model):
    rest_id = models.IntegerField(default=0)
    time = models.DateTimeField(auto_now_add=True)
    lat = models.DecimalField(max_digits=12, decimal_places=2)
    long = models.DecimalField(max_digits=12, decimal_places=2)

class Block(model.Model):
    orders = models.IntegerField(default=0)

class Driver(models.Model):
    name = models.CharField(max_length=100)
    locationList = ArrayField(models.CharField(max_length=100), default=[])