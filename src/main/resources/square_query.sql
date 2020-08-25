ALTER PROCEDURE square_query @lat AS REAL, @long AS REAL, @latdelta AS REAL, @longdelta AS REAL
AS
BEGIN
    SELECT
        count(*) as 'count'
    FROM 
        dbo.doors d
	WHERE
		d.latitude BETWEEN (@lat - @latdelta) and (@lat + @latdelta)
		AND
		d.longitude BETWEEN (@long - @longdelta) and (@long + @longdelta);
	RETURN

END;