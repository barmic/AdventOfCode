#!/usr/bin/env julia

directions = [(-1, -1), (-1, 1), (1, 1), (1, -1)]

directionX(dir, x) = x + dir[1]

directionY(dir, y) = y + dir[2]

invalid(m, x, y) = x < 1 || y < 1 || x > size(m)[1] || y > size(m)[2]

function find(m, x, y, dirs)
    found = String([m[directionX(d, x), directionY(d, y)] for d in dirs if !invalid(m, directionX(d, x), directionY(d, y))])
    if length(found) != 4
        0
    elseif occursin("SS", found) && replace(found, "S" => "") == "MM"
        1
    elseif occursin("MM", found) && replace(found, "M" => "") == "SS"
        1
    else
        0
    end
end

input = readlines(ARGS[1])

A = [input[x][y] for x in 1:length(input), y in 1:length(input[1])]

f=[find(A, idx[1], idx[2], directions) for (idx, c) in pairs(A) if c == 'A']

println(last(accumulate(+, f)))