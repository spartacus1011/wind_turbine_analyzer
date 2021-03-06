function [result, windPercent, windTurbinePercent, otherPercent, confidenceScores]= Classify(imageLocation)

result = "None";

datastore = load('network.mat', 'netTransfer');

network = datastore.netTransfer;

inputSize = network.Layers(1).InputSize;

pixelRange = [-30 30];
imageAugmenter = imageDataAugmenter( ...
    'RandXReflection',true, ...
    'RandXTranslation',pixelRange, ...
    'RandYTranslation',pixelRange);
imdsToClassify = imageDatastore(imageLocation, 'IncludeSubFolders', true, 'LabelSource', 'FolderNames');
imdsToClassifyResized = augmentedImageDatastore(inputSize(1:2),imdsToClassify,'DataAugmentation',imageAugmenter);

[YPred,scores] = classify(network,imdsToClassifyResized);
confidenceScores = scores;

%Pretty verbose, but will work for now
windCount = sum(YPred(:) == 'Wind');
windTurbineCount = sum(YPred(:) == 'Wind Turbine');
otherCount = sum(YPred(:) == 'Other');

totNumber = length(YPred);

%fprintf('wind percentage = %f \n',windCount/totNumber);
%fprintf('wind turbine percentage = %f \n',windTurbineCount/totNumber);

if windCount > windTurbineCount && windCount > otherCount
    result = 'Wind';
elseif windTurbineCount > windCount && windTurbineCount > otherCount
    result = 'Wind Turbine';
elseif otherCount > windCount && otherCount > windTurbineCount
    result = 'Other';
else
    result = 'Error';
end

windPercent = windCount/totNumber;
windTurbinePercent = windTurbineCount/totNumber;
otherPercent = otherCount/totNumber;

end