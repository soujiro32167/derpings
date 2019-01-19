const gcd = require('./index');

function _lcm(a,b){
    return a * b / gcd(a,b);
}

function lcm(...items){
    //console.log('gcd: ', gcd(items));
    return items.reduce((a,b) => _lcm(a, b));
}

module.exports = lcm;