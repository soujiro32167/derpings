const balancedBrackets = require('../index');

test('empty string', () => {
    expect(balancedBrackets('')).toBeTruthy();
})

test('no brackets', () => {
    expect(balancedBrackets('asdad')).toBeTruthy();
})

test('open bracket', () => {
    expect(balancedBrackets('[')).toBeFalsy();
    expect(balancedBrackets('asd[')).toBeFalsy();
    expect(balancedBrackets('[asd')).toBeFalsy();
    expect(balancedBrackets('asd[asd')).toBeFalsy();
})

test('closed bracket', () => {
    expect(balancedBrackets(']')).toBeFalsy();
    expect(balancedBrackets('asd]')).toBeFalsy();
    expect(balancedBrackets(']asd')).toBeFalsy();
    expect(balancedBrackets('asd]asd')).toBeFalsy();
})

test('balanced brackets', () => {
    expect(balancedBrackets('[]')).toBeTruthy();
    expect(balancedBrackets('[asd]')).toBeTruthy();
    expect(balancedBrackets('asd[asd]')).toBeTruthy();
    expect(balancedBrackets('[asd]asd')).toBeTruthy();
    expect(balancedBrackets('asd[asd]asd')).toBeTruthy();
    expect(balancedBrackets('[asd[asd]asd]')).toBeTruthy();
})

test('imbalanced brackets', () => {
    expect(balancedBrackets('[[]')).toBeFalsy();
    expect(balancedBrackets('[]]')).toBeFalsy();
});