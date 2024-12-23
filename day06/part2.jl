#!/usr/bin/env julia

mat = stack(readlines(ARGS[1]))

orig = findfirst(x -> x == '^', mat)
init = (orig[1], orig[2], '^')

function next(dir)
    if dir == '^'
        return '>'
    elseif dir == 'v'
        return '<'
    elseif dir == '>'
        return 'v'
    elseif dir == '<'
        return '^'
    end
    return dir
end

function move(t, map)
    (x, y, d) = t
    if d == '^'
        f = findlast(c -> c == '#', map[x, 1:y-1])
        return f === nothing ? (x, 1, '*') : (x, f + 1, '>')
    elseif d == 'v'
        l = length(map[1, :])
        f = findfirst(c -> c == '#', map[x, y+1:l])
        return f === nothing ? (x, l, '*') :  (x, y + f - 1, '<')
    elseif d == '>'
        l = length(map[:, 1])
        f = findfirst(c -> c == '#', map[x+1:l, y])
        return f === nothing ? (l, y, '*') : (x + f - 1, y, 'v')
    elseif d == '<'
        f = findlast(c -> c == '#', map[1:x-1, y])
        return f === nothing ? (1, y, '*') : (f + 1, y, '^')
    end
end

function try_loop(pos, map)
    guess = copy(map)
    if pos[3] == '^'
        guess[pos[1]-1, pos[2]] = '#'
    elseif pos[3] == 'v'
        guess[pos[1]+1, pos[2]] = '#'
    elseif pos[3] == '>'
        guess[pos[1], pos[2]-1] = '#'
    elseif pos[3] == '<'
        guess[pos[1], pos[2]+1] = '#'
    end

    init = (pos[1], pos[2], next(pos[3]))
    nn = move(init, map)
    while nn !== nothing && nn !== pos && nn[3] !== '*'
        pos = nn
        nn = move(pos, guess)
    end
    if init[1] === 5 && init[2] == 6
        println(init)
        println(nn)
        println("====")
    end
    return init === nn
end

m = '^'
c = init
s = Set()
r = Set()
while m !== '*'
    n = move(c, mat)
    path = reduce(vcat,([(x, y, c[3]) for x in min(c[1], n[1]):max(c[1], n[1]), y in min(c[2], n[2]):max(c[2], n[2])]))

    for p in path
        pp = (p[1], p[2], p[3])
        if try_loop(pp, mat)
            println(pp)
            push!(r, pp)
        end
    end

    for c in path
        push!(s, c)
    end
    global c = n
    global m = c[3]
end

println(length(r))
