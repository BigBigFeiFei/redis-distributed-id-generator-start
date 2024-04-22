local prefix = '__id_auto_generator:';

local step = 3;
local startStep = 2;

local tag = KEYS[1];

local sequence
if redis.call('GET',prefix..tag..':seq') == nil then
	sequence = startStep;
	redis.call('SET',prefix..tag..':seq',startStep);
else
	sequence = tonumber(redis.call('INCRBY', prefix..tag..':seq', step))
end

return string.format(sequence)