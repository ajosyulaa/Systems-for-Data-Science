from flask import Flask, render_template, request, flash, redirect
import torch
from PIL import Image
import ast
from torchvision import transforms
import urllib
import wget

app = Flask(__name__)

url, labelsfile = ("https://raw.githubusercontent.com/ds-umass/project-3-group-9/master/label_names.txt?token=AMEJ3DEJG5VM7SF3ENH2YXS57FZB4", "input_labels.txt")
try: urllib.URLopener().retrieve(url, labelsfile)
except: urllib.request.urlretrieve(url, labelsfile)

@app.route('/')
def hello_world():
    return render_template('classify.html', results=set({}))

@app.route('/search', methods=['GET'])
def classify():
    url = request.args.get('url')
    print(".......this is the url   "  , url)
    filename = wget.download(url)
    
    model = torch.hub.load('pytorch/vision:v0.4.2', 'densenet121', pretrained=True)
    model.eval()
    res = set({})
    
    with open(labelsfile, "r") as data:
        label_dict = ast.literal_eval(data.read())

    input_image = Image.open(filename)
    preprocess = transforms.Compose([
        transforms.Resize(256),
        transforms.CenterCrop(224),
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
    ])
    input_tensor = preprocess(input_image)
    input_batch = input_tensor.unsqueeze(0) # create a mini-batch as expected by the model 

    if torch.cuda.is_available():
        input_batch = input_batch.to('cuda')
        model.to('cuda')

    with torch.no_grad():
        output = model(input_batch)
    print(output[0])
    print(torch.nn.functional.softmax(output[0], dim=0))
    res.add(label_dict[torch.argmax(output[0]).item()])
    
    return render_template('classify.html', results=list(res))

if __name__ == "__main__":
    app.run(host="127.0.0.1", port=int("5000"), debug=True)