#!/usr/bin/env julia

input = readlines(ARGS[1])

orders = [ map(i -> parse(Int, i), split(x, "|")) for x in input if occursin("|", x)]

function myOerders(a, b)
    for order in orders
        if order[1] == a && order[2] == b
            return true
        elseif order[1] == a && order[2] == b
            return false
        end
    end
    return false
end

function weight(line)
    ints = map(i -> parse(Int, i), split(line, ","))
    if sort(ints, lt=myOerders) == ints
        return ints[div(length(ints), 2, RoundUp)]
    else
        return 0
    end
end


println(sum([weight(line) for line in input if occursin(",", line)]))