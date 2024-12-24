#!/usr/bin/env julia

function dots(a, b)
    dx = a[1] - b[1]
    dy = a[2] - b[2]
    if dx == 0 && dy == 0
        return CartesianIndex[]
    end
    [CartesianIndex(a[1] + dx, a[2] + dy), CartesianIndex(b[1] - dx, b[2] - dy)]
end

mat = stack(readlines(ARGS[1]))
spots = [x for x in zip(CartesianIndices(mat), mat) if x[2] != '.']
all_freqs = [[s[1] for s in spots if s[2] == freq] for freq in Set([s[2] for s in spots])]

unique = Set(reduce(vcat, all_freqs .|> x -> Iterators.product(x, x) .|> splat(dots) |> x -> reduce(vcat, x)))

limits = axes(mat)

r = length([x for x in unique if x[1] > 0 && x[2] > 0 && x[1] <= limits[1].stop && x[2] <= limits[2].stop])
println(r)