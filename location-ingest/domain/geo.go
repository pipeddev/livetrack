package domain

import "math"

const earthRadiusKm = 6371.0

func toRad(deg float64) float64 { return deg * math.Pi / 180 }
func toDeg(rad float64) float64 { return rad * 180 / math.Pi }

// HaversineKm: distancia en línea recta sobre la superficie terrestre.
func HaversineKm(a, b Coordinates) float64 {
	dLat := toRad(b.Lat - a.Lat)
	dLng := toRad(b.Lng - a.Lng)
	h := math.Sin(dLat/2)*math.Sin(dLat/2) +
		math.Cos(toRad(a.Lat))*math.Cos(toRad(b.Lat))*math.Sin(dLng/2)*math.Sin(dLng/2)
	return 2 * earthRadiusKm * math.Asin(math.Sqrt(h))
}

// BearingDeg: rumbo de a hacia b (0 = norte, 90 = este).
func BearingDeg(a, b Coordinates) float64 {
	y := math.Sin(toRad(b.Lng-a.Lng)) * math.Cos(toRad(b.Lat))
	x := math.Cos(toRad(a.Lat))*math.Sin(toRad(b.Lat)) -
		math.Sin(toRad(a.Lat))*math.Cos(toRad(b.Lat))*math.Cos(toRad(b.Lng-a.Lng))
	return math.Mod(toDeg(math.Atan2(y, x))+360, 360)
}

// Interpolate: punto en la fracción t (0..1) del trayecto.
// Lineal en lat/lng: suficiente para distancias urbanas/regionales.
func Interpolate(a, b Coordinates, t float64) Coordinates {
	return Coordinates{
		Lat: a.Lat + (b.Lat-a.Lat)*t,
		Lng: a.Lng + (b.Lng-a.Lng)*t,
	}
}