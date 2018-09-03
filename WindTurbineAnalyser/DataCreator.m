clc;
clear;

%spectrogram parms
window = 512;
noverlap = 500;
nfft = 1024;






%{
[signal, fs] = audioread('DownloadedAudio\RealTurbine10Min.mp3');
f = fs/2;

%signal 1 and 2 are the left and right sides
signal1 = signal(1:14400000,1); %trim the signal slightly to make it easier to reshape
signal2 = signal(1:14400000,2);

splitsig1 = reshape(signal1, 144000, 100); %going off the feedback given, maybe we shouldnt divide so evenly. Instead divide randomly so that we give the network different numbers of peaks that it can be trained on
splitsig2 = reshape(signal2, 144000, 100);
 
size = size(splitsig1);

for i = 1:size(2)
    %put the signal stuff here to loop through and seperatly store each of
    %the signal segments
    specData = splitsig1(:,i);
    decimatedData = decimate(specData, 4);
    spectrogram(decimatedData,window,noverlap,nfft,f, 'yaxis', 'onesided');
    colormap jet;
    axis off; 
    caxis([-90 -60]); %Although the cbar is off, this still affects the scale
    colorbar('off');
    img = frame2im(getframe(gca));
    filename = fullfile(strcat('Images\\Wind Turbine\\turbine10min',num2str(i),'.bmp'));
    imwrite(img,filename);
end
%}

%{
[signal, fs] = audioread('DownloadedAudio\turbine135.wav');
f = fs/2;
signal = signal * 3;
%signal 1 and 2 are the left and right sides
signal1 = signal(1:4500000,1); %trim the signal slightly to make it easier to reshape
signal2 = signal(1:4500000,2);

splitsig1 = reshape(signal1, 225000, 20); %going off the feedback given, maybe we shouldnt divide so evenly. Instead divide randomly so that we give the network different numbers of peaks that it can be trained on
splitsig2 = reshape(signal2, 225000, 20);
 
size = size(splitsig1);

for i = 1:size(2)
    %put the signal stuff here to loop through and seperatly store each of
    %the signal segments
    specData = splitsig1(:,i);
    decimatedData = decimate(specData, 4);
    spectrogram(decimatedData,window,noverlap,nfft,f, 'yaxis', 'onesided');
    colormap jet;
    axis off; 
    caxis([-90 -60]); %Although the cbar is off, this still affects the scale
    colorbar('off');
    img = frame2im(getframe(gca)); %Gets rid of the white space on the sides
    filename = fullfile(strcat('Images\\Wind Turbine\\turbine135Amplified',num2str(i),'.bmp'));
    imwrite(img,filename);
    %saveas(img, strcat("Images\\testImage",num2str(i),".bmp"));
end
%}

%{
[signal, fs] = audioread('DownloadedAudio\wind1min.wav');
f = fs/2;

%signal 1 and 2 are the left and right sides
signal1 = signal(1:2800000,1); %trim the signal slightly to make it easier to reshape
signal2 = signal(1:2800000,2);

splitsig1 = reshape(signal1, 140000, 20); %going off the feedback given, maybe we shouldnt divide so evenly. Instead divide randomly so that we give the network different numbers of peaks that it can be trained on
splitsig2 = reshape(signal2, 140000, 20);
 
size = size(splitsig1);

for i = 1:size(2)
    %put the signal stuff here to loop through and seperatly store each of
    %the signal segments
    specData = splitsig1(:,i);
    decimatedData = decimate(specData, 4);
    spectrogram(decimatedData,window,noverlap,nfft,f, 'yaxis', 'onesided');
    colormap jet;
    axis off; 
    caxis([-90 -60]); %Although the cbar is off, this still affects the scale
    colorbar('off');
    img = frame2im(getframe(gca));
    filename = fullfile(strcat('Images\\Wind\\Wind1min',num2str(i),'.bmp'));
    imwrite(img,filename);
    %saveas(img, strcat("Images\\testImage",num2str(i),".bmp"));
end
%}

%{
[signal, fs] = audioread('DownloadedAudio\windforest1min.wav');
f = fs/2;

%signal 1 and 2 are the left and right sides
signal1 = signal(1:2800000,1); %trim the signal slightly to make it easier to reshape
signal2 = signal(1:2800000,2);

splitsig1 = reshape(signal1, 140000, 20); %going off the feedback given, maybe we shouldnt divide so evenly. Instead divide randomly so that we give the network different numbers of peaks that it can be trained on
splitsig2 = reshape(signal2, 140000, 20);
 
size = size(splitsig1);

for i = 1:size(2)
    %put the signal stuff here to loop through and seperatly store each of
    %the signal segments
    specData = splitsig1(:,i);
    decimatedData = decimate(specData, 4);
    spectrogram(decimatedData,window,noverlap,nfft,f, 'yaxis', 'onesided');
    colormap jet;
    axis off; 
    caxis([-90 -60]); %Although the cbar is off, this still affects the scale
    colorbar('off');
    img = frame2im(getframe(gca));
    filename = fullfile(strcat('Images\\Wind\\WindForest',num2str(i),'.bmp'));
    imwrite(img,filename);
    %saveas(img, strcat("Images\\testImage",num2str(i),".bmp"));
end
%}

%{
[signal, fs] = audioread('DownloadedAudio\WindTurbine115.wav');
f = fs/2;

%signal 1 and 2 are the left and right sides
signal1 = signal(1:2800000,1); %trim the signal slightly to make it easier to reshape
signal2 = signal(1:2800000,2);

splitsig1 = reshape(signal1, 140000, 20); %going off the feedback given, maybe we shouldnt divide so evenly. Instead divide randomly so that we give the network different numbers of peaks that it can be trained on
splitsig2 = reshape(signal2, 140000, 20);

size = size(splitsig1);

for i = 1:size(2)
    %put the signal stuff here to loop through and seperatly store each of
    %the signal segments
    specData = splitsig1(:,i);
    decimatedData = decimate(specData, 4);
    spectrogram(decimatedData,window,noverlap,nfft,f, 'yaxis', 'onesided');
    colormap jet;
    axis off; 
    caxis([-90 -60]); %Although the cbar is off, this still affects the scale
    colorbar('off');
    img = frame2im(getframe(gca));
    filename = fullfile(strcat('Images\\Wind Turbine\\windTurbine115',num2str(i),'.bmp'));
    imwrite(img,filename);
end
%}

%{
[signal, fs] = audioread('DownloadedAudio\WindTurbineVarying.wav'); %I like this on because it gives data from different ranges, but I also kinda dont
f = fs/2;

%signal 1 and 2 are the left and right sides
signal1 = signal(1:5000000,1); %trim the signal slightly to make it easier to reshape
signal2 = signal(1:5000000,2);

splitsig1 = reshape(signal1, 125000, 40); %going off the feedback given, maybe we shouldnt divide so evenly. Instead divide randomly so that we give the network different numbers of peaks that it can be trained on
splitsig2 = reshape(signal2, 125000, 40);

size = size(splitsig1);

for i = 1:size(2)
    %put the signal stuff here to loop through and seperatly store each of
    %the signal segments
    specData = splitsig1(:,i);
    decimatedData = decimate(specData, 4);
    spectrogram(decimatedData,window,noverlap,nfft,f, 'yaxis', 'onesided');
    colormap jet;
    axis off; 
    caxis([-90 -60]); %Although the cbar is off, this still affects the scale
    colorbar('off');
    img = frame2im(getframe(gca));
    filename = fullfile(strcat('Images\\Wind Turbine\\VaryingTurbine',num2str(i),'.bmp'));
    imwrite(img,filename);
end
%}

%{
[signal, fs] = audioread('DownloadedAudio\QuietWind.wav');
f = fs/2;

%signal 1 and 2 are the left and right sides
signal1 = signal(1:2800000,1); %trim the signal slightly to make it easier to reshape
signal2 = signal(1:2800000,2);

splitsig1 = reshape(signal1, 140000, 20); %going off the feedback given, maybe we shouldnt divide so evenly. Instead divide randomly so that we give the network different numbers of peaks that it can be trained on
splitsig2 = reshape(signal2, 140000, 20);

size = size(splitsig1);

for i = 1:size(2)
    %put the signal stuff here to loop through and seperatly store each of
    %the signal segments
    specData = splitsig1(:,i);
    decimatedData = decimate(specData, 4);
    spectrogram(decimatedData,window,noverlap,nfft,f, 'yaxis', 'onesided');
    colormap jet;
    axis off; 
    caxis([-90 -60]); %Although the cbar is off, this still affects the scale
    colorbar('off');
    img = frame2im(getframe(gca));
    filename = fullfile(strcat('Images\\Wind\\QuietWind',num2str(i),'.bmp'));
    imwrite(img,filename);
end
%}

%{
[signal, fs] = audioread('DownloadedAudio\Wind5min.wav');
f = fs/2;

%signal 1 and 2 are the left and right sides
signal1 = signal(1:2800000,1); %trim the signal slightly to make it easier to reshape
signal2 = signal(1:2800000,2);

splitsig1 = reshape(signal1, 140000, 20); %going off the feedback given, maybe we shouldnt divide so evenly. Instead divide randomly so that we give the network different numbers of peaks that it can be trained on
splitsig2 = reshape(signal2, 140000, 20);

size = size(splitsig1);

for i = 1:size(2)
    %put the signal stuff here to loop through and seperatly store each of
    %the signal segments
    specData = splitsig1(:,i);
    decimatedData = decimate(specData, 4);
    spectrogram(decimatedData,window,noverlap,nfft,f, 'yaxis', 'onesided');
    colormap jet;
    axis off; 
    caxis([-90 -60]); %Although the cbar is off, this still affects the scale
    colorbar('off');
    img = frame2im(getframe(gca));
    filename = fullfile(strcat('Images\\Wind\\Wind5min',num2str(i),'.bmp'));
    imwrite(img,filename);
end
%}