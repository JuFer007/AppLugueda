document.addEventListener('DOMContentLoaded', function () {
    // 1. Inicializar el mapa en las coordenadas de Chiclayo, Perú
    const map = L.map('map').setView([-6.768, -79.847], 14);

    // 2. Añadir la capa de mapa base de OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    // 3. Ícono personalizado para el autobús
    const busIcon = L.divIcon({
        html: '<i class="fa-solid fa-bus"></i>',
        className: 'bus-marker-icon', // Clase CSS para estilizar el ícono
        iconSize: [30, 30],
        iconAnchor: [15, 15]
    });

    // 4. Definir las rutas de ida (verde) y vuelta (naranja)
    const forwardRoute = [
        [-6.760277, -79.863055], // USAT
        [-6.7618889, -79.8629225], // Hospital Regional Lambayeque
        [-6.7631644, -79.8616334], // Mall Aventura
        [-6.763611, -79.863055], // UTP
        [-6.7720, -79.8400],      // Banco de la Nación
        [-6.7705, -79.8375],      // Mercado Modelo
        [-6.77857, -79.83264]  // Real Plaza Chiclayo
    ];
    // Ruta de vuelta diferente
    const backwardRoute = [
        [-6.77857, -79.83264],  // Real Plaza Chiclayo
        [-6.785, -79.835],      // Un poco más al sur-este
        [-6.790, -79.845],      // Más al sur y oeste
        [-6.780, -79.860],      // Hacia el oeste
        [-6.765, -79.865],      // Acercándose a USAT desde el sur-oeste
        [-6.760277, -79.863055] // USAT (fin de vuelta)
    ];

    // 5. Dibujar las polilíneas de las rutas
    const forwardPolyline = L.polyline(forwardRoute, { color: '#00733c', weight: 5, opacity: 0.8 }).addTo(map);
    const backwardPolyline = L.polyline(backwardRoute, { color: '#dc3545', weight: 5, opacity: 0.8 }).addTo(map);

    // 7. Ajustar el mapa para que se vea toda la ruta
    map.fitBounds(forwardPolyline.getBounds());

    // 8. Lógica de simulación de autobuses
    const buses = [];
    const numberOfBuses = 4; // Número de autobuses en la simulación
    const speed = 0.002; // Velocidad de la animación (un valor más bajo es más lento)

    for (let i = 0; i < numberOfBuses; i++) {
        const isForward = i % 2 === 0;
        const route = isForward ? forwardRoute : backwardRoute;
        
        // Lógica para distribuir los buses a lo largo de la ruta desde el inicio
        let startSegment = 0;
        if (i === 2) { // Segundo bus de ida
            startSegment = Math.floor((forwardRoute.length - 1) / 2);
        } else if (i === 3) { // Segundo bus de vuelta
            startSegment = Math.floor((backwardRoute.length - 1) / 2);
        }
        
        const busData = {
            id: i + 1,
            plate: `ABC-${123 + i}`, // Placa de ejemplo
            speed: Math.floor(Math.random() * (50 - 30) + 30), // Velocidad aleatoria entre 30 y 50 km/h
            marker: L.marker(route[startSegment], { icon: busIcon }).addTo(map),
            route: route,
            segmentIndex: startSegment,
            progress: 0
        };

        // Crea el contenido del popup dinámicamente
        const popupContent = `
            <b>Bus ${busData.id}</b><br>
            Placa: ${busData.plate}<br>
            Velocidad: ${busData.speed} km/h`;
        busData.marker.bindPopup(popupContent);
        buses.push(busData);
    }

    function updateBusPositions() {
        buses.forEach(bus => {
            bus.progress += speed;

            // Si el bus completa un segmento, pasa al siguiente
            if (bus.progress >= 1) {
                bus.progress = 0;
                bus.segmentIndex++;

                // Si el bus llega al final de la ruta, cambia de dirección
                if (bus.segmentIndex >= bus.route.length - 1) {
                    bus.segmentIndex = 0;
                    bus.route = (bus.route === forwardRoute) ? backwardRoute : forwardRoute;
                }
            }

            const currentSegment = bus.route.slice(bus.segmentIndex, bus.segmentIndex + 2);
            const startPoint = currentSegment[0];
            const endPoint = currentSegment[1];

            // Interpolar la posición del bus en el segmento actual
            const lat = startPoint[0] + (endPoint[0] - startPoint[0]) * bus.progress;
            const lng = startPoint[1] + (endPoint[1] - startPoint[1]) * bus.progress;

            bus.marker.setLatLng([lat, lng]);

            // Rotar el ícono del bus (opcional, pero mejora el realismo)
            const angle = Math.atan2(endPoint[0] - startPoint[0], endPoint[1] - startPoint[1]) * 180 / Math.PI;
            bus.marker.setRotationAngle(angle + 90); // Leaflet.RotatedMarker no está incluido, esto es un ejemplo
        });
    }

    // Iniciar la animación
    setInterval(updateBusPositions, 50); // Actualiza la posición cada 50ms
});