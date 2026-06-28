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
        it('should call /CCMS/dashboard/count with params', function() {
            $httpBackend.expectGET('/CCMS/dashboard/count?distrtict=ALL&mandal=ALL&gp=ALL')
                .respond({ total_devices: 100 });

            dashboardFactory.getAllCount('?distrtict=ALL&mandal=ALL&gp=ALL');
        });

        it('should return count data', function() {
            var responseData = { total_devices: 50, light_on: 40, light_off: 10 };
            $httpBackend.whenGET('/CCMS/dashboard/count?distrtict=ALL&mandal=ALL&gp=ALL')
                .respond(responseData);

            var result;
            dashboardFactory.getAllCount('?distrtict=ALL&mandal=ALL&gp=ALL')
                .then(function(data) { result = data.data; });
            $httpBackend.flush();

            expect(result).toEqual(responseData);
        });

        it('should handle empty response', function() {
            $httpBackend.whenGET('/CCMS/dashboard/count?distrtict=&mandal=&gp=')
                .respond({});

            dashboardFactory.getAllCount('?distrtict=&mandal=&gp=');
        });

        it('should handle server error', function() {
            $httpBackend.whenGET('/CCMS/dashboard/count?bad=param')
                .respond(500);

            dashboardFactory.getAllCount('?bad=param');
        });
    });

    describe('getAllMapDashboardData', function() {
        it('should call /CCMS/dashboard/map_data with params', function() {
            $httpBackend.expectGET('/CCMS/dashboard/map_data?distrtict=ALL&mandal=ALL&gp=ALL')
                .respond([]);

            dashboardFactory.getAllMapDashboardData('?distrtict=ALL&mandal=ALL&gp=ALL');
        });

        it('should return map data array', function() {
            var mapData = [{ lat: '16.4792', lang: '80.5469', light_status: 1 }];
            $httpBackend.whenGET('/CCMS/dashboard/map_data?distrtict=ALL&mandal=ALL&gp=ALL')
                .respond(mapData);

            var result;
            dashboardFactory.getAllMapDashboardData('?distrtict=ALL&mandal=ALL&gp=ALL')
                .then(function(data) { result = data.data; });
            $httpBackend.flush();

            expect(result).toEqual(mapData);
        });

        it('should handle empty map data', function() {
            $httpBackend.whenGET('/CCMS/dashboard/map_data?distrtict=ALL&mandal=ALL&gp=ALL')
                .respond([]);

            dashboardFactory.getAllMapDashboardData('?distrtict=ALL&mandal=ALL&gp=ALL');
        });
    });

    describe('getAllDcuNames', function() {
        it('should call /CCMS/dcu/dcu_name_list with params', function() {
            $httpBackend.expectGET('/CCMS/dcu/dcu_name_list?distrtict=ALL&mandal=ALL&gp=ALL')
                .respond([{ name: 'DCU-001' }]);

            dashboardFactory.getAllDcuNames('?distrtict=ALL&mandal=ALL&gp=ALL');
        });
    });

    describe('getByMondal', function() {
        it('should call /CCMS/filter/get_mandal with district', function() {
            $httpBackend.expectGET('/CCMS/filter/get_mandal?distrtict=Guntur-17')
                .respond(['Tenali', 'Guntur Rural']);

            dashboardFactory.getByMondal('Guntur-17');
        });

        it('should return mandal list', function() {
            var mandals = ['Tenali', 'Guntur Rural'];
            $httpBackend.whenGET('/CCMS/filter/get_mandal?distrtict=Guntur-17')
                .respond(mandals);

            var result;
            dashboardFactory.getByMondal('Guntur-17')
                .then(function(data) { result = data.data; });
            $httpBackend.flush();

            expect(result).toEqual(mandals);
        });
    });

    describe('getByGp', function() {
        it('should call /CCMS/filter/get_gp with mandal', function() {
            $httpBackend.expectGET('/CCMS/filter/get_gp?mandal=Tenali')
                .respond(['GP1', 'GP2']);

            dashboardFactory.getByGp('Tenali');
        });
    });


});
