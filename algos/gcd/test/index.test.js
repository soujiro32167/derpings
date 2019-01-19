const gcd = require('../index');
const lcm = require('../lcm');


test('gcd(a,0) = a', () => {
    expect(gcd(10, 0)).toBe(10);
    expect(gcd(456, 0)).toBe(456);
});

test('gcd(a,a) = a', () => {
    expect(gcd(10,10)).toBe(10);
    expect(gcd(456, 456)).toBe(456);
});

test('general cases', () => {
    expect(gcd(54, 24)).toBe(6);
    expect(gcd(2,3)).toBe(1);
    expect(gcd(36, 20)).toBe(4);
})

test('multiple numbers', () => {
    expect(gcd(54, 108, 60)).toBe(6);
    expect(gcd(16, 32, 96)).toBe(16);
});

test('lcm', () => {
    expect(lcm(2, 4, 4)).toBe(4);
})