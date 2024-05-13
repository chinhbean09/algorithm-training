function processHeaderAndComments(input) {
    var lines = input.split('\n');
    var result = '';
    var inHeader = false;
    var inFooter = false;
    var headerStartIndex = -1;
    var headerEndIndex = -1;

    lines.forEach((line, index) => {
        var processedLine = line.trim();

        if (processedLine.startsWith('//') && processedLine.includes(';')) {
            if (processedLine.length > 2) {
                processedLine = processedLine.split(';')[0].trim();
                processedLine = processedLine.replace(/'/g, '');
                result += processedLine + '\n'; 
            }
            return; 
        }

        if (processedLine === '//') {
            return;
        }

        if (!inHeader && (/^\/\/\*{3,}/.test(processedLine) || /\/\/-{3,}\*$/.test(processedLine) || processedLine.startsWith('/*'))) {
            if (processedLine == '/*'){
                result += processedLine.replace(/^\/{2}\*+/, '/*') + '\n';
                inHeader = true;
                return;
            }
            
            if (lines[index + 1] && !lines[index + 1].endsWith('*') ) {
                return;
            }
            
            inHeader = true;
            result += processedLine.replace(/^\/{2}\*+/, '/*') + '\n';
                return; 
            }
        
        
        if (inHeader && (/^\/\/\*{3,}/.test(processedLine) || /\/\/-{3,}\*$/.test(processedLine) || processedLine.startsWith('*/'))) {
            if (lines[index + 1] === '' || !lines[index + 1].endsWith('*') || /\/\/\*[^*]*\*[^/]/.test(lines[index + 1]) || /\*+$/.test(lines[index + 1])) {
                headerEndIndex = index;
                if (lines[index + 1].trim().endsWith('/')) {
                    inHeader = true;
                } else if(lines[index + 1].trim().endsWith('*')){
                    inHeader = true;
                } else {
                    inHeader = false;
                    result += processedLine.replace(/.*/, '*/') + '\n';
                }
                return;
            } else {
                inHeader = false;
                result += processedLine.replace(/.*/, '*/') + '\n';
                return;
            }
            

        } 

        if (inHeader) {
            if (/^\/\/\*{3,}/.test(processedLine) || /\/\/-{3,}\*$/.test(processedLine) || processedLine.startsWith('/*')) {
                return; 
            }
            
            if (processedLine.startsWith('//*')) {
                processedLine = processedLine.replace(/^\/\//, '').replace(/'/g, '').trim();
            } else if (processedLine.startsWith('//')) {
                processedLine = processedLine.replace(/^\/\//, '*').replace(/'/g, '').trim();
            }
            if (processedLine.endsWith('*')) {
                processedLine = processedLine.replace(/\*+$/, '').trim().replace(/'/g, '').trim();
            }
            if (processedLine.startsWith('*')) {
                processedLine = processedLine.replace(/'/g, '').trim();
            }
            if (!lines[index + 1].startsWith('//')){
                if(lines[index + 1].startsWith('*')){
                    result += processedLine + '\n';
                    return;
                }
                processedLine += '\n' + '*/'
                inHeader = false;
            }
            result += processedLine + '\n';
            return;
        }
        
        
    
        var matchColon = processedLine.match(/\/\/\s*(.*):\s*(.*)/); 
        if (matchColon !== null) {
            var field = matchColon[1].trim();
            var value = matchColon[2].trim().replace(/\*/g, ''); 
            result += '     ' + field + ' : ' + value + '\n';
            return; 
        }

    if (!inHeader && processedLine.startsWith('//')) {
        if (processedLine.startsWith('//*')) {
            var replacedLine = processedLine.replace(/\*/g, '');
            result += replacedLine.trim() + '\n';            
        } else {
            if (processedLine.startsWith('*')) {
                result += processedLine.substring(1).replace(/'/g, '').trim() + '\n';
            } else {
                result += processedLine.replace(/'/g, '').trim() + '\n';
            }
        }
    } else {
        result += processedLine + '\n';
        return result; 
    }
    });

    return result;
}

var input = `
....
.....
...
`;

var output = processHeaderAndComments(input);
console.log(output);
