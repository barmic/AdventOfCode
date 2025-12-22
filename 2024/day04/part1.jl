#!/usr/bin/env julia

directions = [(dx, dy) for dx in -1:1, dy in -1:1 if (dx, dy) != (0, 0)]

directionX(dir, x) = x + dir[1]

directionY(dir, y) = y + dir[2]

function find(m, x, y, word, dirs)
    if x < 1 || y < 1 || x > size(m)[1] || y > size(m)[2] || m[x, y] != first(word)
        0
    else
        rest=String(chop(word, head=1, tail=0))
        if length(rest) == 0
            1
        else
            last(accumulate(+, [find(m, directionX(d, x), directionY(d, y), rest, [d]) for d in dirs]))
        end
    end
end

input = readlines(ARGS[1])

A = [input[x][y] for x in 1:length(input), y in 1:length(input[1])]

result = last(accumulate(+, [find(A, idx[1], idx[2], "XMAS", directions) for (idx, c) in pairs(A)]))

println(result)