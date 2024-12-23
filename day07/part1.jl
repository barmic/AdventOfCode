#!/usr/bin/env julia

function validate(target, data)
    heads = [0]
    i = 1
    while length(heads) > 0 && i <= length(data)
        heads = vcat([ [head * data[i], head + data[i]] for head in heads ]...)
        i += 1
    end
    if target in heads
        return target
    else
        return 0
    end
end

function parseLine(line)
    ints = map(i -> parse(Int, i), split(replace(line, ":" => ""), " ", keepempty = false))
    return (ints[1], ints[2:length(ints)])
end

r = sum(map(a -> validate(a[1], a[2]), map(l -> parseLine(l), readlines(ARGS[1]))))
println(r)