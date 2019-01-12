from django.contrib import admin

# Register your models here.
from .models import DriverLocation, Block

admin.site.register(DriverLocation)
admin.site.register(Block)