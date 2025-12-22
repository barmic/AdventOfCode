#!/usr/bin/env julia

function div(x, y)
    r = x / y
    return isinteger(r) ? r : -1
end

function validate(target, data)
    heads = [0]
    for d in data
        heads = vcat([ [head * d, head + d] for head in heads ]...)
    end
    return target in heads ? target : 0
end

function parseLine(line)
    ints = map(i -> parse(Int, i), split(replace(line, ":" => ""), " ", keepempty = false))
    return (ints[1], ints[2:length(ints)])
end

r = sum(map(a -> validate(a[1], a[2]), map(parseLine, readlines(ARGS[1]))))
println(r)
