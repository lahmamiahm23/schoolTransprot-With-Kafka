import React, { useEffect, useRef } from 'react';

const VehicleMap = ({ position }) => {
    const mapRef = useRef(null);

    useEffect(() => {
        if (position?.available && position.position) {
            // Simuler l'affichage d'une carte
            console.log('Position du bus:', position.position);

            if (mapRef.current) {
                mapRef.current.innerHTML = `
          <div style="
            background: #e8f4f8;
            height: 100%;
            width: 100%;
            position: relative;
            border-radius: 8px;
            overflow: hidden;
          ">
            <div style="
              position: absolute;
              left: 50%;
              top: 50%;
              transform: translate(-50%, -50%);
              background: white;
              padding: 10px;
              border-radius: 50%;
              box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            ">
              <div style="font-size: 24px;">🚌</div>
            </div>
            
            <div style="
              position: absolute;
              bottom: 10px;
              left: 10px;
              background: white;
              padding: 5px 10px;
              border-radius: 4px;
              font-size: 12px;
            ">
              Lat: ${position.position.latitude.toFixed(4)}<br/>
              Lng: ${position.position.longitude.toFixed(4)}
            </div>
          </div>
        `;
            }
        }
    }, [position]);

    return (
        <div className="vehicle-map">
            {position?.available ? (
                <div ref={mapRef} className="map-container" style={{ height: '300px' }} />
            ) : (
                <div className="no-position">
                    <p>🚫 Aucune position disponible</p>
                    <p>Le GPS du véhicule n'est pas actif</p>
                </div>
            )}
        </div>
    );
};

export default VehicleMap;
