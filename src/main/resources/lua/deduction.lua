local key = KEYS[1];
local deduction = tonumber(ARGV[1]);
local total = redis.call("hget", key, "total_num");
local used = redis.call("hget", key, "used_num");
if total == nil or used == nil then
    return 0;
else
    local totalNum = tonumber(total);
    local usedNum = tonumber(used);
    if totalNum - usedNum >= deduction then
        redis.call("hset", key, "used_num", usedNum + deduction);
        return 1;
    else
        return 0;
    end;
end;