#!/usr/bin/env julia

mat = stack(readlines(ARGS[1]))
limits = axes(mat)

inMap(xy) = xy[1] > 0 && xy[2] > 0 && xy[1] <= limits[1].stop && xy[2] <= limits[2].stop

function dots(a, b)
    dx = a[1] - b[1]
    dy = a[2] - b[2]
    if dx == 0 && dy == 0
        return CartesianIndex[]
    end
    r = CartesianIndex[]
    for op in [+, -]
        i = 1
        while true
            next = CartesianIndex(op(a[1], i * dx), op(a[2], i * dy))
            i += 1
            if inMap(next)
                push!(r, next)
            else
                break
            end
        end
    end
    return r
end

spots = [x for x in zip(CartesianIndices(mat), mat) if x[2] != '.']
all_freqs = [[s[1] for s in spots if s[2] == freq] for freq in Set([s[2] for s in spots])]

unique = Set(reduce(vcat, all_freqs .|> x -> Iterators.product(x, x) .|> splat(dots) |> x -> reduce(vcat, x)))


r = length([x for x in unique if inMap(x)])
println(r)