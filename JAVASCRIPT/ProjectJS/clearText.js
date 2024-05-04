function removeUnnecessaryText(input) {
    var lines = input.split('\n');
    var result = [];

    lines.forEach(line => {
        var processedLine = line.trim();

        if (processedLine.startsWith('//') && processedLine.indexOf(';') !== -1) {
            if (processedLine.length > 2) {
                processedLine = processedLine.split(';')[0].trim();
                processedLine = processedLine.replace(/'/g, '');
                result.push(processedLine); 
            }
            return; 
        }

        if (processedLine.indexOf('//') !== -1) {
            processedLine = processedLine.split('//')[0].trim();
        }

        result.push(processedLine);
    });

    return result.join('\n');
}

function applyJavaCodingRules(input) {
    var lines = input.split('\n');
    var result = [];
    var insideClass = false;
    var classDepth = 0;

    lines.forEach(line => {
        var processedLine = line.trim();

        if (processedLine.startsWith('public class') || processedLine.startsWith('private class')) {
            insideClass = true;
            result.push(processedLine);
            classDepth++; // Bắt đầu một khối lớp mới, tăng mức độ lồng
        } else if (insideClass) {
            if (processedLine.startsWith('{')) {
                // Bắt đầu một khối code mới, tăng mức độ lồng
                result.push(' '.repeat(classDepth * 4) + processedLine);
                classDepth++;
            } else if (processedLine.startsWith('}')) {
                // Kết thúc khối code, giảm mức độ lồng
                classDepth--;
                result.push(' '.repeat(classDepth * 4) + processedLine);
                if (classDepth === 0) {
                    insideClass = false; // Kết thúc khối lớp
                }
            } else {
                // Thụt dòng vào trong khối lớp
                result.push(' '.repeat(classDepth * 4) + processedLine);
            }
        } else {
            result.push(line); // Giữ nguyên các dòng bên ngoài khối lớp
        }
    });

    return result.join('\n');
}

var input = `Tự custom commnent`;

var cleanedInput = removeUnnecessaryText(input);
var output = applyJavaCodingRules(cleanedInput);

console.log(output);
