# Speech-to-Text Service

This service utilizes the library from [AlphaCEPHEI](https://alphacephei.com/vosk/). You can find the library's GitHub repository for different languages [here](https://github.com/alphacep/vosk-api/tree/master). The service is developed in Java 17 with the Spring Framework and is a Spring Boot application.

## Prerequisites
- **Operating System**: Ubuntu 23.04 (Other systems can be used with user-specific adjustments)
- **Java**: Version 17 or higher
- **Maven**: Apache Maven 3.8.7
- **Docker**: Version 24.0.6, build ed223bc
- **Microk8s**: Client Version: v1.27.5
- **Helm**: v3.12.3+g3a31588

## Setup

1. **Build**
    - Execute the `build.sh` script to build the service, package the artifacts in Docker, and push the Docker image to the microk8s repository.

2. **Microk8s Installation**
```
sudo snap install microk8s --classic
sudo usermod -a -G microk8s $USER
sudo chown -f -R $USER ~/.kube
sudo systemctl start snap.microk8s.daemon-kubelet
microk8s enable helm3
microk8s enable dns
microk8s enable ingress
microk8s enable metallb
microk8s enable dashboard
```


3. **Access Microk8s**
   - Use [this link](http://127.0.0.1:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/) to access the Microk8s dashboard.

4. **Helm Deployment**
```
microk8s kubectl create namespace speech-to-text
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install my-ingress ingress-nginx/ingress-nginx -n speech-to-text
helm install speech-to-text . -n speech-to-text
```

5. **Update Host Configuration**
    - Add the following line to `/etc/hosts`:
```
192.168.2.220 speech-to-text.local
```

## Usage

Upload an audio recording to the designated directory and navigate to the service URL. You should then see a textual interpretation of the audio content.

## Configuration (`values.yaml`)

```yaml
speech:
  dirPath: "/path/to/data"
  modelPath: "/path/to/model"
  sampleRate: "44100"
  logLevel: "Info"
  hostDirPath: "/home/${USER}/voice"
  hostModelPath: "/home/${USER}/model"
```

For models in various languages, refer to this [this link](https://alphacephei.com/vosk/models).


ARM64_SPECIFIC=/Users/dmitriybalasn/vosk-api/src/libvosk.dylib;DYLD_LIBRARY_PATH=/Users/dmitriybalasn/vosk-api/src:/opt/homebrew/opt/gcc/lib/gcc/current;SPEECH_DIR_PATH=/Users/dmitriybalasn/Yandex.Disk.localized/dic;SPEECH_MODEL_PATH=/Users/dmitriybalasn/model
