#!/usr/bin/env julia

function div(x, y)
    r = x / y
    return isinteger(r) ? r : -1
end

function validate(target, data)
    heads = [target]
    i = length(data)
    while length(heads) > 0 && i > 0
        newHeads = Int[]
        for head in heads
            r1 = div(head, data[i])
            if isinteger(r1)
                push!(newHeads, r1)
            end
            r2 = head - data[i]
            if r2 >= 0
                push!(newHeads, r2)
            end
        end
        heads = newHeads
        i -= 1
    end
    return i == 0 && 0 in heads ? target : 0
end

function parseLine(line)
    ints = map(i -> parse(Int, i), split(replace(line, ":" => ""), " ", keepempty = false))
    return (ints[1], ints[2:length(ints)])
end

lines = [parseLine(line) for line in readlines(ARGS[1])]

@time sum(map(a -> validate(a[1], a[2]), lines))
@time sum(map(a -> validate(a[1], a[2]), lines))
@time sum(map(a -> validate(a[1], a[2]), lines))

opti = map(a -> validate(a[1], a[2]), lines)
println(sum(opti))
