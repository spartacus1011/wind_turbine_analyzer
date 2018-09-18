function y = CreateClassificationImages(audioFileLocation,imageLocation, window, noverlap,nfft)

y = 0;

%Clearing old classification Images
filePattern = fullfile(imageLocation, '*.bmp'); % Change to whatever pattern you need.
[fPath, fName, fExt] = fileparts(imageLocation);
theFiles = dir(filePattern);
for k = 1 : length(theFiles)
  baseFileName = theFiles(k).name;
  fullFileName = fullfile(imageLocation, baseFileName);
  %fprintf(1, 'Now deleting %s\n', fullFileName);
  delete(fullFileName);
end

%Creating classification Images
minSeconds = 2;
maxSeconds = 5;  

[signal, fs] = audioread(fullfile(audioFileLocation));
f=fs/2;

roundedLength = floor(length(signal)/1000)*1000;

totSeconds = floor(roundedLength/f); %dont know why we use f and not fs, but fs works

currentPos = 1;
currentSegment = 1; %like the i an the for loop

while currentPos < (roundedLength - (maxSeconds*fs))
    secondsLength = randi([minSeconds maxSeconds]);
    dataLength = secondsLength * fs;
    specData = signal(currentPos:currentPos + dataLength);

    decimatedData = decimate(specData, 4);
    spectrogram(decimatedData,window,noverlap,nfft,f, 'yaxis', 'onesided');
    colormap jet;
    axis off; 
    caxis([-90 -60]); %Although the cbar is off, this still affects the scale
    colorbar('off');
    img = frame2im(getframe(gca));
    filename = fullfile(strcat(imageLocation,'\\', fName,num2str(currentSegment),'.bmp'));
    imwrite(img,filename);
    
    currentPos= currentPos + dataLength;
    currentSegment = currentSegment + 1;
end %while

y = 1;

end
%}

