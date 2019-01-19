module.exports = (l1, l2) => {
    let i1 = 0, 
        i2 = 0,
        x1 = l1[i1],
        x2 = l2[i2],
        result = [];

    while (i1 < l1.length && i2 < l2.length){
        let prevEqual;
        while (x1 <= x2 && i1 < l1.length){
            //console.log('x1 x2', x1, x2);
            result.push(x1);
            i1++;
            x1 = l1[i1];
        }
    
        while (x2 < x1 && i2 < l2.length){
            //console.log('x1 x2', x1, x2);
            result.push(x2);
            i2++;
            x2 = l2[i2];
        }
    }

    if (i1 == l1.length){
        //console.log('l2: ',l2);
        //console.log('l2 sliced: ',l2.slice(i2, l2.length));
        result.push.apply(result, l2.slice(i2, l2.length));
    } else {
        //console.log('l1: ',l1);
        //console.log('l1 sliced: ', l1.slice(i1, l1.length));
        result.push.apply(result, l1.slice(i1, l1.length));
    }

    return result;
}