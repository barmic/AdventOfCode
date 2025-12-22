#!/usr/bin/env julia

mat = stack(readlines(ARGS[1]))

orig = findfirst(x -> x == '^', mat)
init = (orig[1], orig[2], '^')

function move(t)
    (x, y, d) = t
    if d == '^'
        f = findlast(c -> c == '#', mat[x, 1:y-1])
        return f === nothing ? (x, 1, '*') : (x, f + 1, '>')
    elseif d == 'v'
        l = length(mat[1, :])
        f = findfirst(c -> c == '#', mat[x, y+1:l])
        return f === nothing ? (x, l, '*') :  (x, y + f - 1, '<')
    elseif d == '>'
        l = length(mat[:, 1])
        f = findfirst(c -> c == '#', mat[x+1:l, y])
        return f === nothing ? (l, y, '*') : (x + f - 1, y, 'v')
    elseif d == '<'
        f = findlast(c -> c == '#', mat[1:x-1, y])
        return f === nothing ? (1, y, '*') : (f + 1, y, '^')
    end
end

c = init
s = Set()
while c[3] !== '*'
    n = move(c)
    for c in reduce(vcat,([(x, y) for x in min(c[1], n[1]):max(c[1], n[1]), y in min(c[2], n[2]):max(c[2], n[2])]))
        push!(s, c)
    end
    global c = n
end
println(length(s))