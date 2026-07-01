describe('dashboardFactory', function() {
    var $httpBackend, dashboardFactory;

    beforeEach(module('dashboardFactory'));
    beforeEach(module('inform'));

    beforeEach(inject(function(_$httpBackend_, _dashboardFactory_) {
        $httpBackend = _$httpBackend_;
        dashboardFactory = _dashboardFactory_;
    }));

    afterEach(function() {
        if ($httpBackend) {
            try { $httpBackend.flush(); } catch(e) {}
        }
    });

    describe('getAllCount', function() {
        it('should call /dashboard/count with params', function() {
            $httpBackend.expectGET('/dashboard/count?district=ALL&mandal=ALL&gp=ALL')
                .respond({ total_devices: 100 });

            dashboardFactory.getAllCount('?district=ALL&mandal=ALL&gp=ALL');
        });

        it('should return count data', function() {
            var responseData = { total_devices: 50, light_on: 40, light_off: 10 };
            $httpBackend.whenGET('/dashboard/count?district=ALL&mandal=ALL&gp=ALL')
                .respond(responseData);

            var result;
            dashboardFactory.getAllCount('?district=ALL&mandal=ALL&gp=ALL')
                .then(function(data) { result = data.data; });
            $httpBackend.flush();

            expect(result).toEqual(responseData);
        });

        it('should handle empty response', function() {
            $httpBackend.whenGET('/dashboard/count?district=&mandal=&gp=')
                .respond({});

            dashboardFactory.getAllCount('?district=&mandal=&gp=');
        });

        it('should handle server error', function() {
            $httpBackend.whenGET('/dashboard/count?bad=param')
                .respond(500);

            dashboardFactory.getAllCount('?bad=param');
        });
    });

    describe('getAllMapDashboardData', function() {
        it('should call /dashboard/map_data with params', function() {
            $httpBackend.expectGET('/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL')
                .respond([]);

            dashboardFactory.getAllMapDashboardData('?district=ALL&mandal=ALL&gp=ALL');
        });

        it('should return map data array', function() {
            var mapData = [{ lat: '16.4792', lang: '80.5469', light_status: 1 }];
            $httpBackend.whenGET('/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL')
                .respond(mapData);

            var result;
            dashboardFactory.getAllMapDashboardData('?district=ALL&mandal=ALL&gp=ALL')
                .then(function(data) { result = data.data; });
            $httpBackend.flush();

            expect(result).toEqual(mapData);
        });

        it('should handle empty map data', function() {
            $httpBackend.whenGET('/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL')
                .respond([]);

            dashboardFactory.getAllMapDashboardData('?district=ALL&mandal=ALL&gp=ALL');
        });
    });

    describe('getAllDcuNames', function() {
        it('should call /dcu/dcu_name_list with params', function() {
            $httpBackend.expectGET('/dcu/dcu_name_list?district=ALL&mandal=ALL&gp=ALL')
                .respond([{ name: 'DCU-001' }]);

            dashboardFactory.getAllDcuNames('?district=ALL&mandal=ALL&gp=ALL');
        });
    });

    describe('getByMandal', function() {
        it('should call /filter/get_mandal with district', function() {
            $httpBackend.expectGET('/filter/get_mandal?district=Guntur-17')
                .respond(['Tenali', 'Guntur Rural']);

            dashboardFactory.getByMandal('Guntur-17');
        });

        it('should return mandal list', function() {
            var mandals = ['Tenali', 'Guntur Rural'];
            $httpBackend.whenGET('/filter/get_mandal?district=Guntur-17')
                .respond(mandals);

            var result;
            dashboardFactory.getByMandal('Guntur-17')
                .then(function(data) { result = data.data; });
            $httpBackend.flush();

            expect(result).toEqual(mandals);
        });
    });

    describe('getByGp', function() {
        it('should call /filter/get_gp with mandal', function() {
            $httpBackend.expectGET('/filter/get_gp?mandal=Tenali')
                .respond(['GP1', 'GP2']);

            dashboardFactory.getByGp('Tenali');
        });
    });


});
