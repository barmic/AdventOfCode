#!/usr/bin/env julia

struct State
    data::Vector{Int64}
    files::Int64
    bits::Int64
end

struct Input
    data::Vector{Int64}
end

function checksumpart(x::Int64, n::Int64, l::Int64)
    a = (l * (l - 1)) ÷ 2
    r = x * (l * n + a)
    return r
end

function Base.iterate(iter::Input)
    cpy = copy(iter.data)
    size = cpy[1]
    result = checksumpart(0, 0, size)
    cpy[1] = 0
    return (result, State(cpy, 2, size))
end

function Base.iterate(iter::Input, state::State)
    if state.files > length(state.data)
        return nothing
    elseif state.files % 2 == 1 && state.data[state.files] > 0
        size = state.data[state.files]
        result = checksumpart(state.files ÷ 2, state.bits, size)
        state.data[state.files] = 0
        return (result, State(state.data, state.files + 1, state.bits + size))
    elseif state.files % 2 == 1 && state.data[state.files] < 0
        return iterate(iter, State(state.data, state.files + 1, state.bits + abs(state.data[state.files])))
    elseif state.files % 2 == 0
        selected = filter(x -> isodd(x[1]) && 0 < x[2] <= state.data[state.files], collect(enumerate(state.data)))
        if length(selected) <= 0
            return (0, State(state.data, state.files + 1, state.bits + state.data[state.files]))
        elseif selected[end][1] < state.files
            return nothing
        elseif state.data[state.files] > 0
            size = min(state.data[selected[end][1]], state.data[state.files])
            result = checksumpart(selected[end][1] ÷ 2, state.bits, size)
            state.data[state.files] -= size
            state.data[selected[end][1]] *= -1
            next = state.data[state.files] > 0 ? 0 : 1
            return (result, State(state.data, state.files + next, state.bits + size))
        end
    end
    return iterate(iter, State(state.data, state.files + 1, state.bits + state.data[state.files]))
end

line = map(x -> parse(Int, x), split(readlines(ARGS[1])[1], ""))

init = Input(line)

@time println(sum(x for x in init))
