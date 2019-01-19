const mergeSortedLists = require('../index');

test('empty lists give an empty list', () => {
    expect(mergeSortedLists([], [])).toEqual([]);
});

test('non-empty + empty = non-empty', () => {
    expect(mergeSortedLists([1,2,3], [])).toEqual([1,2,3]);
    expect(mergeSortedLists([], [1,2,3])).toEqual([1,2,3]);
});

test('full lists: no intersection', () => {
    expect(mergeSortedLists([1,2,3], [4,5,6])).toEqual([1,2,3,4,5,6]);
});

test('full lists: intersection', () => {
    expect(mergeSortedLists([1,2,3,100,101,102], [4,5,6,100])).toEqual([1,2,3,4,5,6,100,100,101,102]);
})

test('full lists: intersection with duplicates', () => {
    expect(mergeSortedLists([1,2,3,3,100,101,102], [4,5,6,100])).toEqual([1,2,3,3,4,5,6,100,100,101,102]);
})