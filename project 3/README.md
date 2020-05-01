# Image Classification Server

This project involves the creation of a dockerised image for an Image Classification application using pytorch. The following are the steps to build and run the docker image. 

To build the docker image: 
1. Clone the repository.
2. Change directory to project-3-group-9 of the repo. 
3. Run the following command to build the docker image:
    docker build --tag docker_image_name .

This will setup the environment required for running the image classification server (this might take a short while). After the docker image is successfully build, we can list it using the command:
     docker images

To run the docker image that was generated or an existing docker image in the repo:
1. Run the command:
    docker run -p 5000:5000 -i docker_image_name
2. We can see the dockers currently running using the command:
    docker ps
3. Now we have the server running. So we can use the application on http://127.0.0.1:5000/ or localhost on port 5000. 
4. Enter the url of any image and click "submit". We can then see the classification for this image on imagenet label space. 

To run the code without docker image:
1. Run index.py file.
2. Now we have the server running. So we can use the application on http://127.0.0.1:5000/ or localhost on port 5000.
3. Enter the url of any image and click "submit". We can then see the classification for this image on imagenet label space.
     

