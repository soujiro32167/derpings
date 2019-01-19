/**
 * 
 * @param {Number} a 
 * @param {Number} b
 * @return {Number} The highest number that divides both a and b
 */
function _gcd(a,b){
    //console.log('a b', a, b);
    return b === 0 ? a : _gcd(b, a % b);
}

function gcd(...N){
    return N.reduce((a,b) => _gcd(a,b));
}

module.exports = gcd;