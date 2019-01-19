const OPEN_BRACKET = '[';
const CLOSED_BRACKET = ']';

module.exports = (string) => {
    // c == '[' => push
    // c == ']' => pop. If empty stack, imbalanced
    // c !== '[' && c !== ']' => no effect
    let stack = [];
    
    for (c of string){
        if (c === '['){
            stack.push(c);
        } else if (c === ']'){
            if (stack.length === 0){
                return false;
            } else {
                stack.pop();
            }
        }
    }

    return stack.length === 0;
}