describe('mapFactory', function() {
    var $httpBackend, mapViewFactory;

    beforeEach(module('mapFactory'));
    beforeEach(module('inform'));

    beforeEach(inject(function(_$httpBackend_, _mapViewFactory_) {
        $httpBackend = _$httpBackend_;
        mapViewFactory = _mapViewFactory_;
    }));

    afterEach(function() {
        if ($httpBackend) {
            try { $httpBackend.flush(); } catch(e) {}
        }
    });

    describe('getAllCount', function() {
        it('should call /CCMS/dashboard/count with query params', function() {
            $httpBackend.expectGET('/CCMS/dashboard/count?district=ALL&mandal=ALL&gp=ALL')
                .respond({ total_devices: 100 });

            mapViewFactory.getAllCount('?district=ALL&mandal=ALL&gp=ALL');
        });

        it('should return count data', function() {
            var response = { total_devices: 50, light_on: 40 };
            $httpBackend.whenGET('/CCMS/dashboard/count?district=ALL&mandal=ALL&gp=ALL')
                .respond(response);

            var result;
            mapViewFactory.getAllCount('?district=ALL&mandal=ALL&gp=ALL')
                .then(function(data) { result = data.data; });
            $httpBackend.flush();

            expect(result).toEqual(response);
        });

        it('should handle empty response', function() {
            $httpBackend.whenGET('/CCMS/dashboard/count?x=y')
                .respond({});

            mapViewFactory.getAllCount('?x=y');
        });
    });

    describe('getAllMapDashboardData', function() {
        it('should call /CCMS/dashboard/map_data with query params', function() {
            $httpBackend.expectGET('/CCMS/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL')
                .respond([]);

            mapViewFactory.getAllMapDashboardData('?district=ALL&mandal=ALL&gp=ALL');
        });

        it('should return map data array', function() {
            var mapData = [{ lat: '16.4792', lang: '80.5469', light_status: 1 }];
            $httpBackend.whenGET('/CCMS/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL')
                .respond(mapData);

            var result;
            mapViewFactory.getAllMapDashboardData('?district=ALL&mandal=ALL&gp=ALL')
                .then(function(data) { result = data.data; });
            $httpBackend.flush();

            expect(result).toEqual(mapData);
        });

        it('should handle empty map data', function() {
            $httpBackend.whenGET('/CCMS/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL')
                .respond([]);

            mapViewFactory.getAllMapDashboardData('?district=ALL&mandal=ALL&gp=ALL');
        });
    });

    describe('getByMandal', function() {
        it('should call /CCMS/filter/get_mandal with district', function() {
            $httpBackend.expectGET('/CCMS/filter/get_mandal?district=Guntur-17')
                .respond(['Tenali']);

            mapViewFactory.getByMandal('Guntur-17');
        });
    });

    describe('getByGp', function() {
        it('should call /CCMS/filter/get_gp with mandal', function() {
            $httpBackend.expectGET('/CCMS/filter/get_gp?mandal=Tenali')
                .respond(['GP1']);

            mapViewFactory.getByGp('Tenali');
        });
    });
});
