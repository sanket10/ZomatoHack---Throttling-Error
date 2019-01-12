from django.http import HttpResponse
from rest_framework import permissions, status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

def index(request):
    return HttpResponse("Hey! This is the Heatmap Back Health Check Endpoint! All looks good here!")


class update_location(APIView):
    """
        GET: For viewing all products
    """
    authentication_classes = ()
    permission_classes = ()

    def get(self, request):
        """
        :return: Response containing JSON data, with all the details of single products.
        """
        return Response({'status': ''}, status=status.HTTP_200_OK)

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
        return Response({'status': ''}, status=status.HTTP_200_OK)

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
        return Response({'status': ''}, status=status.HTTP_200_OK)
