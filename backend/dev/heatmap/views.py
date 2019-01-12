from django.http import HttpResponse
from rest_framework import permissions, status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from . import models
from . import utils
from decimal import Decimal
from django.core.exceptions import ObjectDoesNotExist


def index(request):
    return HttpResponse("Hey! This is the Heatmap Back Health Check Endpoint! All looks good here!")


class update_location(APIView):
    """
        GET: Update Location of a driver in database
    """
    authentication_classes = ()
    permission_classes = ()

    def get(self, request):
        """
        :return: Health API check
        """
        return Response({'status': 'All good!'}, status=status.HTTP_200_OK)
    
    def post(self, request):
        """
        :return: Update Driver Location
        """
        data = request.data
        # serializer = DriverLocationSerializer(data = data)
        # if serializer.is_valid():
        #     serializer.save()
        req_key = ['id', 'latitude', 'longitude']
        
        id = int(data[req_key[0]])
        lat = Decimal(data[req_key[1]])
        lon = Decimal(data[req_key[2]])

        try:
            driver = models.DriverLocation.objects.get(pk=id)
            old_lat = driver.latitude
            old_lon = driver.longitude

            old_block = utils.find_block(old_lat, old_lon)
            new_block = utils.find_block(lat, lon)

            old_block.resources = old_block.resources - 1
            new_block.resources = new_block.resources + 1
            old_block.save()
            new_block.save()

            driver.latitude = lat
            driver.longitude = lon

            driver.save()
        except ObjectDoesNotExist:
            new_obj = models.DriverLocation.objects.create(longitude=lon, latitude=lat)
            new_obj.save()
            return Response({'id': id, 'lat': lat , 'lon': lon}, status=status.HTTP_200_OK)

        return Response({'status': request.data}, status=status.HTTP_200_OK)

class get_heatmap(APIView):
    """
        GET: For viewing all products
    """
    authentication_classes = ()
    permission_classes = ()

    def get(self, request):
        """
        :return: Response containing JSON data, with all the details of single products.
        """
        ny = 'NewYearData'
        s = 'SundayData'
        m = 'MondayData'

        # Get Data from ML
        numerator = utils.get_csv_data(ny)
        
        # Get RealTime Driver Stats
        denominator = utils.get_driver_stats()

        final_data = [n/d for n,d in zip(numerator, denominator)]
        lat_lon = utils.get_all_lat_lon()
        s = ""
        for d in lat_lon:
            s = s + str(d) + "," 
        for v in final_data:
            s = s + str(v) + ","
        return Response({'response': s}, status=status.HTTP_200_OK)

class get_next_data(APIView):
    """
        GET: For viewing all products
    """
    authentication_classes = ()
    permission_classes = ()

    def get(self, request):
        """
        :return: Response containing JSON data, with all the details of single products.
        """
        utils.shuffle_data()
        return Response({'status': ''}, status=status.HTTP_200_OK)
