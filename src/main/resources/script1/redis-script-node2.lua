local prefix = '__idgenerator:';

local step = 3;
local startStep = 1;

local tag = KEYS[1];
local y_m_d_h_m_s = KEYS[2];
local len = KEYS[3];
local y_m_d_h_m_s_len = string.len(y_m_d_h_m_s);

local y_m_d_h_m_s_key = redis.call('GET',prefix..tag..':y_m_d_h_m_s');

if y_m_d_h_m_s_key ~= y_m_d_h_m_s then
	if y_m_d_h_m_s_key and (y_m_d_h_m_s - y_m_d_h_m_s_key) > 0 then
		redis.call('SET',prefix..tag..':y_m_d_h_m_s', y_m_d_h_m_s);
		redis.call('SET',prefix..tag..':seq',startStep);
	elseif not y_m_d_h_m_s_key then
	    redis.call('SET',prefix..tag..':y_m_d_h_m_s', y_m_d_h_m_s);
		redis.call('SET',prefix..tag..':seq',startStep);
	end
end
local sequence
if redis.call('GET',prefix..tag..':seq') == nil then
	sequence = startStep;
	redis.call('SET',prefix..tag..':seq',startStep);
else
	sequence = tonumber(redis.call('INCRBY', prefix..tag..':seq', step))
end

return string.format('%0'..y_m_d_h_m_s_len ..'d', redis.call('GET',prefix..tag..':y_m_d_h_m_s'))..string.format('%0'..len..'d', sequence)